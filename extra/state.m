width = 20;

grid = zeros(1, width + 2);
grid(1) = 16;

its = 500;

for it = 1:its
    for i = 2:width
        grid(i) = round(mean([grid(i-1) grid(i) grid(i+1)]));
    end
end

figure
plot(grid);

%[X, Y] = meshgrid(1:width + 2, 1:width + 2);
%surf(X, Y, grid);
