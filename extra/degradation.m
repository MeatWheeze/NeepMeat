clear
len = 0.1 * 3600 * 20;
y = zeros(1, len);
x = 1:len;
for i = 1:len
    if (i > 1)
        rate = 0.1 * (1 - y(i - 1))^4;
        y(i) = y(i - 1) + rate;
    end
end

tau = (x)(y>=0.75)(1);

figure
plot(x / (50*3600), y * 100)
hold on
plot([tau tau] / (20*3600) , [0, 1] * 100)
xlabel("Time (hr)")
ylabel("Degradation (%)")
