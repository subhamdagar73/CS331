{-# OPTIONS_GHC -Wno-unused-top-binds #-}
-- HW_MiniDraw.hs
-- Name: Subham
-- Student ID: 230101098

module HW_MiniDraw where

import Data.Monoid

-- =====================
-- BASIC TYPES
-- =====================

type Program = [Cmd]
type Point   = (Int, Int)
type Scene   = [(Shape, Point)]

data Expr
  = Val Int
  | Add Expr Expr
  | Mul Expr Expr
  deriving (Eq, Show)

data Shape
  = Circle Expr
  | Rect Expr Expr
  deriving (Eq, Show)

data Cmd
  = Draw Shape
  | Move Expr Expr
  deriving (Eq, Show)

-- =====================
-- PART 1: Expressions
-- =====================

-- | >>> evalExpr (Add (Val 2) (Mul (Val 3) (Val 4)))
-- 14
evalExpr :: Expr -> Int
evalExpr (Val n)     = n
evalExpr (Add e1 e2) = evalExpr e1 + evalExpr e2
evalExpr (Mul e1 e2) = evalExpr e1 * evalExpr e2

-- =====================
-- PART 2: Functor
-- =====================

data AnnShape a
  = ACircle a Int
  | ARect a Int Int
  deriving (Eq, Show)

instance Functor AnnShape where
  -- Transforms annotations while preserving shape geometry
  fmap f (ACircle ann r)   = ACircle (f ann) r
  fmap f (ARect   ann w h) = ARect   (f ann) w h

-- =====================
-- PART 3: Applicative Validation
-- =====================

data Validation e a
  = Failure e
  | Success a
  deriving (Eq, Show)

instance Functor (Validation e) where
  fmap _ (Failure e) = Failure e
  fmap f (Success a) = Success (f a)

instance Semigroup e => Applicative (Validation e) where
  pure = Success
  (Failure e1) <*> (Failure e2) = Failure (e1 <> e2)
  (Failure e1) <*> _            = Failure e1
  _            <*> (Failure e2) = Failure e2
  (Success f)  <*> (Success a)  = Success (f a)

-- | >>> validateExpr (Val 5)
-- Success 5
validateExpr :: Expr -> Validation [String] Int
validateExpr e =
  let val = evalExpr e
  in if val > 0
     then Success val
     else Failure ["Value must be positive: " ++ show val]

validateShape :: Shape -> Validation [String] Shape
validateShape (Circle r) =
  Circle . Val <$> validateExpr r
validateShape (Rect w h) =
  Rect <$> (Val <$> validateExpr w) <*> (Val <$> validateExpr h)

-- =====================
-- PART 4: Monad Execution
-- =====================

newtype Exec a = Exec { runExec :: Point -> Either String (a, Point, Scene) }

instance Functor Exec where
  fmap f (Exec m) = Exec $ \p ->
    case m p of
      Left err         -> Left err
      Right (a, p', s) -> Right (f a, p', s)

instance Applicative Exec where
  pure a = Exec $ \p -> Right (a, p, [])
  (Exec mf) <*> (Exec ma) = Exec $ \p ->
    case mf p of
      Left err          -> Left err
      Right (f, p', s1) ->
        case ma p' of
          Left err           -> Left err
          Right (a, p'', s2) -> Right (f a, p'', s1 ++ s2)

instance Monad Exec where
  (Exec m) >>= f = Exec $ \p ->
    case m p of
      Left err          -> Left err
      Right (a, p', s1) ->
        case runExec (f a) p' of
          Left err           -> Left err
          Right (b, p'', s2) -> Right (b, p'', s1 ++ s2)

-- | Executes a single command, updating position or adding to scene
runCmd :: Cmd -> Exec ()
runCmd (Move ex ey) = Exec $ \(cx, cy) ->
  let dx = evalExpr ex
      dy = evalExpr ey
  in Right ((), (cx + dx, cy + dy), [])
runCmd (Draw s) = Exec $ \pos ->
  Right ((), pos, [(s, pos)])

-- | Executes a full program starting from (0,0)
-- | >>> runProgram [Draw (Circle (Val 5))]
-- Right [(Circle (Val 5),(0,0))]
runProgram :: Program -> Either String Scene
runProgram prog =
  case runExec (mapM_ runCmd prog) (0, 0) of
    Left err          -> Left err
    Right (_, _, scene) -> Right scene

-- =====================
-- PART 5: Monoid Logging
-- =====================

newtype Log = Log [String]
  deriving (Eq, Show)

instance Semigroup Log where
  (Log l1) <> (Log l2) = Log (l1 ++ l2)

instance Monoid Log where
  mempty = Log []

-- Extended output with logs
data Out = Out
  { outScene :: Scene
  , outLog   :: Log
  } deriving (Eq, Show)

instance Semigroup Out where
  (Out s1 l1) <> (Out s2 l2) = Out (s1 ++ s2) (l1 <> l2)

instance Monoid Out where
  mempty = Out [] mempty

newtype ExecLog a = ExecLog { runExecLog :: Point -> Either String (a, Point, Out) }

instance Functor ExecLog where
  fmap f (ExecLog m) = ExecLog $ \p ->
    case m p of
      Left err           -> Left err
      Right (a, p', out) -> Right (f a, p', out)

instance Applicative ExecLog where
  pure a = ExecLog $ \p -> Right (a, p, mempty)
  (ExecLog mf) <*> (ExecLog ma) = ExecLog $ \p ->
    case mf p of
      Left err            -> Left err
      Right (f, p', out1) ->
        case ma p' of
          Left err             -> Left err
          Right (a, p'', out2) -> Right (f a, p'', out1 <> out2)

instance Monad ExecLog where
  (ExecLog m) >>= f = ExecLog $ \p ->
    case m p of
      Left err            -> Left err
      Right (a, p', out1) ->
        case runExecLog (f a) p' of
          Left err             -> Left err
          Right (b, p'', out2) -> Right (b, p'', out1 <> out2)

-- | Executes a single command with logging
runCmdLog :: Cmd -> ExecLog ()
runCmdLog (Move ex ey) = ExecLog $ \(x, y) ->
  let dx     = evalExpr ex
      dy     = evalExpr ey
      newPos = (x + dx, y + dy)
      logMsg = Log ["Moved to " ++ show newPos]
  in Right ((), newPos, Out [] logMsg)
runCmdLog (Draw s) = ExecLog $ \pos ->
  let logMsg = case s of
        Circle r   -> Log ["Drew Circle at " ++ show pos
                           ++ " radius " ++ show (evalExpr r)]
        Rect w h   -> Log ["Drew Rect at "   ++ show pos
                           ++ " w=" ++ show (evalExpr w)
                           ++ " h=" ++ show (evalExpr h)]
  in Right ((), pos, Out [(s, pos)] logMsg)

-- | Executes a full program with logging, starting from (0,0)
runProgramLog :: Program -> Either String Out
runProgramLog prog =
  case runExecLog (mapM_ runCmdLog prog) (0, 0) of
    Left err            -> Left err
    Right (_, _, out)   -> Right out

-- =====================
-- PART 6: Optimization
-- =====================

-- | Simplifies arithmetic expressions to single literals.
optimizeExpr :: Expr -> Expr
optimizeExpr e = Val (evalExpr e)

optimizeCmd :: Cmd -> Cmd
optimizeCmd (Draw s)   = Draw $ case s of
  Circle r -> Circle (optimizeExpr r)
  Rect w h -> Rect   (optimizeExpr w) (optimizeExpr h)
optimizeCmd (Move x y) = Move (optimizeExpr x) (optimizeExpr y)

optimizeProg :: Program -> Program
optimizeProg = map optimizeCmd

-- =====================
-- SAMPLE PROGRAM
-- =====================

example :: Program
example =
  [ Draw (Circle (Val 5))
  , Move (Val 10) (Val 20)
  , Draw (Rect (Val 4) (Val 6))
  ]