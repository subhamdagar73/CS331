% ----------------------------------------
% N-Queens using CLP(FD)
% ----------------------------------------

:- use_module(library(clpfd)).

% queens(N, Sol) : Sol represents positions of N non-attacking queens
queens(N, Sol) :-
    length(Sol, N),
    Sol ins 1..N,
    place_queens(Sol).

% Recursively ensure all queens are safe
place_queens([]).
place_queens([Row | Rest]) :-
    check_conflicts(Rest, Row, 1),
    place_queens(Rest).

% No two queens attack each other
check_conflicts([], _, _).
check_conflicts([R | Rs], R0, Dist) :-
    R #\= R0,
    abs(R - R0) #\= Dist,
    NextDist is Dist + 1,
    check_conflicts(Rs, R0, NextDist).

% ----------------------------------------
% Search Strategies
% ----------------------------------------

% Generate and Test approach
gen_test(N, Sol) :-
    length(Sol, N),
    maplist(between(1, N), Sol),
    queens(N, Sol).

% Early Pruning approach
prune_first(N, Sol) :-
    queens(N, Sol),
    maplist(between(1, N), Sol).

% Intelligent Search using labeling
smart_search(N, Sol, Options) :-
    queens(N, Sol),
    labeling(Options, Sol).

% ----------------------------------------
% Labeling options examples:
% []       -> leftmost (default)
% [ff]     -> first fail
% [ffc]    -> most constrained
% ----------------------------------------
