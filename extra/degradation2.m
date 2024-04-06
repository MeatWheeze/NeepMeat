clear
len = 2 * 3600 * 20;
k = 0.0002;
h = 1;
max_its = len / h;

x = 1:h:len;
y = zeros(1, length(x));

i = 0;
while i < max_its
    if (i > 1)
         rate = k * 1500 / 300 * (1 - y(i-1))^4;
        y(i) = y(i-1) + h * rate;
        % if y(i) >= 0.75
        %     printf("Found RUL at %0.1fhr in %i its\n", x(i) / (20*3600), i);
        %     break
        % end
    end

    i = i + 1;
end

[tau_est, its] = estimate_degradation(0, k, 20, 5000);


figure
hold on
plot(x / (20*3600), y * 100)
xlabel("Time (hr)")
ylabel("Degradation (%)")

greater = (x)(y>=0.75);
if length(greater) > 0
    tau = greater(1);
    plot([tau tau] / (20*3600) , [0, 1] * 100)
    printf("True RUL: %.2fhr\n", tau / (20*3600))
    printf("Estimated RUL: %.2fhr\n", tau_est / (20*3600))
else
    printf("RUL exceeds specified interval.\n")
end
