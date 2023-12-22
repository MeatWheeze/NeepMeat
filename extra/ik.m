T = [1, 1.5];
P1 = [0 0];
line([P1(1) T(1)], [P1(2) T(2)], 'linestyle', '--');

lt = 2.3125;

D5 = norm(T - P1);

XY = T ./ D5 .* min(D5, 2*lt - 0.0001);

D = norm(XY);

D1 = XY ./ D;

A = acos(((-D^2) / (-2 * D * lt)))

D3 = lt .* (D1(1) * cos(A) - D1(2) * sin(A));
D4 = lt .* (D1(1) * sin(A) + D1(2) * cos(A));

line([P1(1) D3], [P1(2) D4], 'linestyle', '-');
line([D3 T(1)], [D4 T(2)], 'linestyle', '-');
