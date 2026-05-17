module Assignment_5 where

-- | A simple polymorphic Min Heap
data MinHeap a = Empty | Node a (MinHeap a) (MinHeap a)
    deriving (Show, Eq)

-- | Check whether the heap is empty.
isEmpty :: MinHeap a -> Bool
isEmpty Empty = True
isEmpty _     = False

-- | Compute the total number of elements in the heap.
size :: MinHeap a -> Int
size Empty          = 0
size (Node _ l r)   = 1 + size l + size r

-- | Return the minimum element (root) of the heap.
--   Assume the heap is not empty.
findMin :: MinHeap a -> a
findMin (Node x _ _) = x
findMin Empty        = error "Heap is empty"

-- | Convert the heap into a list using preorder traversal
heapToList :: MinHeap a -> [a]
heapToList Empty          = []
heapToList (Node x l r)   = x : (heapToList l ++ heapToList r)

-- | Check whether a heap satisfies the min-heap property.
--   A node must be less than or equal to its children
isHeap :: Ord a => MinHeap a -> Bool
isHeap Empty = True
isHeap (Node x l r) = 
    check x l && check x r && isHeap l && isHeap r
  where
    check _ Empty        = True
    check p (Node c _ _) = p <= c

-- | Helper function to merge two heaps while maintaining the min-heap property.
merge :: Ord a => MinHeap a -> MinHeap a -> MinHeap a
merge h1 Empty = h1
merge Empty h2 = h2
merge h1@(Node x l1 r1) h2@(Node y l2 r2)
    | x <= y    = Node x (merge r1 h2) l1
    | otherwise = Node y (merge r2 h1) l2

-- | Insert an element into the heap while maintaining heap property
insertHeap :: Ord a => a -> MinHeap a -> MinHeap a
insertHeap x h = merge (Node x Empty Empty) h

-- | Delete the minimum element (root) from the heap
deleteMin :: Ord a => MinHeap a -> MinHeap a
deleteMin Empty        = Empty
deleteMin (Node _ l r) = merge l r

-- | Return all even elements from the heap using list comprehension.
evenHeap :: MinHeap Int -> [Int]
evenHeap h = [x | x <- heapToList h, even x]

-- | Apply a function to every element in the heap.
mapHeap :: (a -> a) -> MinHeap a -> MinHeap a
mapHeap _ Empty          = Empty
mapHeap f (Node x l r)   = Node (f x) (mapHeap f l) (mapHeap f r)

-- | Process the heap by extracting even elements and squaring them.
processHeap :: MinHeap Int -> [Int]
processHeap h = [x * x | x <- heapToList h, even x]


-- | Main Function

main :: IO ()
main = do
    putStrLn "Enter some numbers:"
    input <- getLine
    let nums = map read (words input) :: [Int]
    
    -- 1. Testing insertHeap and foldr
    let heap = foldr insertHeap Empty nums
    putStrLn $ "\nBuilt heap from input: " ++ show heap
    
    -- 2. Testing isEmpty
    putStrLn $ "Is the heap empty? " ++ show (isEmpty heap)
    
    -- 3. Testing size
    putStrLn $ "Number of elements: " ++ show (size heap)
    
    -- 4. Testing isHeap
    putStrLn $ "Does it satisfy min-heap property? " ++ show (isHeap heap)
    
    -- 5. Testing findMin (with safety check)
    if not (isEmpty heap)
        then putStrLn $ "Current minimum (root): " ++ show (findMin heap)
        else putStrLn "Heap is empty, no minimum."

    -- 6. Testing heapToList
    putStrLn $ "Preorder traversal: " ++ show (heapToList heap)

    -- 7. Testing deleteMin
    let afterDelete = deleteMin heap
    putStrLn $ "Heap after removing root: " ++ show afterDelete

    -- 8. Testing merge
    -- Create a second heap to merge with the original
    let secondHeap = foldr insertHeap Empty [10, 0, 15]
    let mergedHeap = merge heap secondHeap
    putStrLn $ "Merged with [10, 0, 15]: " ++ show mergedHeap

    -- 9. Testing evenHeap
    putStrLn $ "Even numbers in heap: " ++ show (evenHeap heap)

    -- 10. Testing mapHeap
    let incrementedHeap = mapHeap (+100) heap
    putStrLn $ "Adding 100 to all elements: " ++ show incrementedHeap

    -- 11. Testing processHeap
    putStrLn $ "Squares of even numbers: " ++ show (processHeap heap)
    