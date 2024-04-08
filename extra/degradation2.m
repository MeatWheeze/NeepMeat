clear
len = 5 * 3600 * 20;
k1 = 0.0002;
k0 = 0.0001;
h = 10;
max_its = len / h;

x = 1:h:len;
y = zeros(1, length(x));

%rate_func = @(y) k1 * 1500 / 300 * (y + 0.01)^1.5;
rate_func = @(y) k0 * 1 * (y + 0.01)^1.5;

i = 0;
while i < max_its
    if (i > 1)
        % rate = k * 1500 / 300 * (1 - y(i-1))^0.5;
        %rate = k * 1500 / 300 * (y(i-1) + 0.01)^4;
        rate = rate_func(y(i-1));
        y(i) = min(y(i-1) + h * rate, 1);
        % if y(i) >= 0.75
        %     printf("Found RUL at %0.1fhr in %i its\n", x(i) / (20*3600), i);
        %     break
        % end
    end

    i = i + 1;
end

[tau_est, its] = estimate_degradation(0, 20 * 50, 1000, rate_func);


figure
hold on
plot(x / (20*3600), y * 100)
xlabel("Time (hr)")
ylabel("Performance Degradation (%)")

greater = (x)(y>=0.75);
if length(greater) > 0
    tau = greater(1);
    plot([tau tau] / (20*3600) , [0, 1] * 100)
    printf("True RUL: %.2fhr\n", tau / (20*3600))
    printf("Estimated RUL: %.2fhr\n", tau_est / (20*3600))
else
    printf("RUL exceeds specified interval.\n")
end
