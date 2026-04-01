%-------------------------------------Task 1 -------------------------------------------
% Description: Flatten a nested list into a single-level list

% Base case: An empty list flattens to an empty list
flatten([], []).

% Recursive case 1: If the head is a list, flatten it and concatenate with flattened tail

flatten([Head|Tail], FlatList) :-
    is_list(Head),
    flatten(Head, FlatHead),
    flatten(Tail, FlatTail),
    append(FlatHead, FlatTail, FlatList).

% Recursive case 2: If the head is not a list, keep it and flatten the tail

flatten([Head|Tail], [Head|FlatTail]) :-
    \+ is_list(Head),
    flatten(Tail, FlatTail).


%-----------------------------------Task 2-----------------------------------------------
% Description: Translate a list of digits to their English word equivalents

means(0, zero). 
means(1, one). 
means(2, two). 
means(3, three). 
means(4, four). 
means(5, five).
means(6, six). 
means(7, seven). 
means(8, eight). 
means(9, nine).


% Base case: An empty list translates to an empty list
translate([], []).

% Recursive case: Translate the head using means/2, then recursively translate the tail
translate([N|Nums], [W|Words]) :-
    means(N, W),
    translate(Nums, Words).

%-----------------------------------Task 3-----------------------------------------------
% Description: Demonstrate custom operator definitions and parsing in Prolog

% 1. Define operators first
% op(Precedence, Associativity, OperatorName)

% - 'was' is an infix operator (xfx) with precedence 500
:- op(500, xfx, was).

% - 'the' is a prefix operator (fy) with precedence 300
:- op(300, fy, the).

% - 'of' is a right-associative infix operator (yfx) with precedence 400
:- op(400, yfx, of).

% 2. Then define the fact using the custom operators
joe was the head of the department.


%-----------------------------------Task 4-----------------------------------------------
% Description: Find all subsets of a list that sum to a target value

% Base case: a sum of 0 is reached with an empty subset
subsum(_, 0, []).

% Recursive case 1: Include the head element in the subset
subsum([H|T], Sum, [H|Sub]) :-
    Sum > 0,
    NewSum is Sum - H,
    subsum(T, NewSum, Sub).

% Recursive case 2: Exclude the head element and try the tail
subsum([_|T], Sum, Sub) :-
    Sum > 0,
    subsum(T, Sum, Sub).

%-----------------------------------Task 5-----------------------------------------------
% Description: Generate integers between two bounds (inclusive)

% Base case: If Num1 is within the range [Num1, Num2], unify X with Num1
between(Num1, Num2, Num1) :- 
    Num1 =< Num2.

% Recursive case: Move to the next integer and recurse
% If Num1 < Num2, increment Num1 and try again
between(Num1, Num2, X) :- 
    Num1 < Num2,
    Next is Num1 + 1,
    between(Next, Num2, X).