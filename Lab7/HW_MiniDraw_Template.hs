{-# OPTIONS_GHC -Wno-unused-top-binds #-}
-- HW_MiniDraw.hs
-- Name:
-- Student ID:

module HW_MiniDraw where

import Data.Monoid

-- =====================
-- BASIC TYPES
-- =====================

type Program = [Cmd]
type Point = (Int, Int)
type Scene = [(Shape, Point)]

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
evalExpr = undefined

-- =====================
-- PART 2: Functor
-- =====================

data AnnShape a
  = ACircle a Int
  | ARect a Int Int
  deriving (Eq, Show)

instance Functor AnnShape where
  fmap = undefined

-- =====================
-- PART 3: Applicative Validation
-- =====================

data Validation e a
  = Failure e
  | Success a
  deriving (Eq, Show)

instance Functor (Validation e) where
  fmap = undefined

instance Monoid e => Applicative (Validation e) where
  pure = undefined
  (<*>) = undefined

-- | >>> validateExpr (Val 5)
-- Success 5
validateExpr :: Expr -> Validation [String] Int
validateExpr = undefined

validateShape :: Shape -> Validation [String] Shape
validateShape = undefined

-- =====================
-- PART 4: Monad Execution
-- =====================

type Exec a = Point -> Either String (a, Point, Scene)

runCmd :: Cmd -> Exec ()
runCmd = undefined

-- | >>> runProgram [Draw (Circle (Val 5))]
-- Right [(Circle (Val 5),(0,0))]
runProgram :: Program -> Either String Scene
runProgram = undefined

-- =====================
-- PART 5: Monoid Logging
-- =====================

newtype Log = Log [String]
  deriving (Eq, Show)

instance Semigroup Log where
  (<>) = undefined

instance Monoid Log where
  mempty = undefined

-- =====================
-- PART 6: Optimization
-- =====================

optimizeExpr :: Expr -> Expr
optimizeExpr = undefined

optimizeCmd :: Cmd -> Cmd
optimizeCmd = undefined

optimizeProg :: Program -> Program
optimizeProg = undefined

-- =====================
-- SAMPLE PROGRAM
-- =====================

example :: Program
example =
  [ Draw (Circle (Val 5))
  ,
 Move (Val 10) (Val 20)
  ,
 Draw (Rect (Val 4) (Val 6))
  ]
