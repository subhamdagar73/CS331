module Assignment_5 where

-- | A simple polymorphic Min Heap
data MinHeap a = Empty | Node a (MinHeap a) (MinHeap a)
    deriving (Show, Eq)


-- | Check whether the heap is empty.
--
-- >>> isEmpty Empty
-- True
--
-- >>> isEmpty (Node 5 Empty Empty)
-- False
--
isEmpty :: MinHeap a -> Bool
isEmpty = undefined



-- | Compute the total number of elements in the heap.
--
-- >>> size Empty
-- 0
--
-- >>> size (Node 5 (Node 3 Empty Empty) Empty)
-- 2
--
-- >>> size (Node 5 (Empty) Empty)
-- 1
--
size :: MinHeap a -> Int
size = undefined


-- | Return the minimum element (root) of the heap.
--   Assume the heap is not empty.
--
-- >>> findMin (Node 2 Empty Empty)
-- 2
--
-- >>> findMin (Node 1 (Node 3 Empty Empty) Empty)
-- 1
--
findMin :: MinHeap a -> a
findMin = undefined


-- | Convert the heap into a list using preorder traversal.
--
-- >>> heapToList (Node 1 (Node 2 Empty Empty) (Node 3 Empty Empty))
-- [1,2,3]
--
heapToList :: MinHeap a -> [a]
heapToList = undefined


-- | Check whether a heap satisfies the min-heap property.
--   A node must be less than or equal to its children.
--
-- >>> isHeap (Node 1 (Node 2 Empty Empty) (Node 3 Empty Empty))
-- True
--
-- >>> isHeap (Node 5 (Node 2 Empty Empty) Empty)
-- False
--
isHeap :: Ord a => MinHeap a -> Bool
isHeap = undefined


-- | Helper function to merge two heaps.
merge :: Ord a => MinHeap a -> MinHeap a -> MinHeap a
merge = undefined


-- | Insert an element into the heap while maintaining heap property.
--
-- >>> insertHeap 2 (Node 5 Empty Empty)
-- Node 2 (Node 5 Empty Empty) Empty
--
insertHeap :: Ord a => a -> MinHeap a -> MinHeap a
insertHeap = undefined


-- | Delete the minimum element (root) from the heap.
--
-- >>> deleteMin (Node 1 (Node 2 Empty Empty) (Node 3 Empty Empty))
-- Node 2 (Node 3 Empty Empty) Empty
--

deleteMin :: Ord a => MinHeap a -> MinHeap a
deleteMin = undefined


-- | Return all even elements from the heap using list comprehension.
--
-- >>> evenHeap (Node 1 (Node 2 Empty Empty) (Node 4 Empty Empty))
-- [2,4]
--
evenHeap :: MinHeap Int -> [Int]
evenHeap = undefined


-- | Apply a function to every element in the heap.
--
-- >>> mapHeap (*2) (Node 1 Empty Empty)
-- Node 2 Empty Empty
--

-- >>> mapHeap (*2) (Node 1 (Node 4 Empty Empty) Empty)
-- Node 2 (Node 8 Empty Empty) Empty
--
mapHeap :: (a -> a) -> MinHeap a -> MinHeap a
mapHeap = undefined


-- | Process the heap by extracting even elements and squaring them.
--
-- >>> processHeap (Node 2 (Node 3 Empty Empty) (Node 4 Empty Empty))
-- [4,16]
--
processHeap :: MinHeap Int -> [Int]
processHeap = undefined


-- | Interactive program:
--   Reads numbers, builds heap, and prints results
--
main :: IO ()
main = undefined
