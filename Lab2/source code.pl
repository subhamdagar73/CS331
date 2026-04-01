% -----------------TASK 1: split(Numbers, Positives, Negatives)-----------------------------
% --- Version 1: Without Cut ---
% Uses mutually exclusive boundary conditions (H >= 0 and H < 0) 
% to ensure correct results during backtracking.

% Base case: An empty list results in two empty lists.
split([], [], []).

% If the head (H) >=0, add it to the Positives list.
split([H|T], [H|Pos], Neg) :-
    H >= 0,
    split(T, Pos, Neg).

% If the head (H) <0, add it to the Negatives list.
split([H|T], Pos, [H|Neg]) :-
    H < 0,
    split(T, Pos, Neg).


% --- Version 2: With Cut (!) ---
% The cut (!) operator prevents Prolog from backtracking to the second 
% rule if the first condition (H >= 0) has already been met.

split_cut([], [], []).

split_cut([H|T], [H|Pos], Neg) :-
    H >= 0, !, % Cut: If H >= 0, commit to this choice point.
    split_cut(T, Pos, Neg).

% This rule is only reached if H >= 0 failed.
split_cut([H|T], Pos, [H|Neg]) :-
    split_cut(T, Pos, Neg).


% -------------------------TASK 2: Train Routes & Knowledge Base----------------------------
directTrain(guwahati, tezpur).
directTrain(nagaon, guwahati).
directTrain(lumding, nagaon).
directTrain(haflong, lumding).
directTrain(silchar, haflong).
directTrain(agartala, silchar).
directTrain(aizawl, agartala).

% Symmetric relationship: Add information that trains are bidirectional
connected(X, Y) :- directTrain(X, Y).
connected(X, Y) :- directTrain(Y, X).

% route/3: Returns a list of towns (Path) between Start and End
route(Start, End, Path) :-
    travel(Start, End, [Start], Path).

% travel/4: Helper predicate using an accumulator (Visited) to avoid infinite loops.
% Base case: Destination reached.
travel(End, End, Visited, Path) :- 
    reverse(Visited, Path). % Reverse to show route in Start -> End order.

% Recursive case: Move to an adjacent town that hasn't been visited yet.
travel(Current, End, Visited, Path) :-
    connected(Current, Next),
    \+ member(Next, Visited), % Negation as Failure: avoids cycles.
    travel(Next, End, [Next|Visited], Path).



% ----------------------------TASK 3: number_of_parents Logic Correction--------------------------
% PROBLEMS WITH INITIAL IMPLEMENTATION:
% 1. Query: number_of_parents(X, 0)
%    -> Returns X = adam only (eve is skipped)
%
% 2. Query: number_of_parents(adam, 2)
%    -> returns true 
%


% Corrected version using explicit inequality (\=) or cuts.
number_of_parents(adam, 0).
number_of_parents(eve, 0).
number_of_parents(X, 2) :-
    X \= adam, % Ensure X is not adam
    X \= eve.  % Ensure X is not eve
