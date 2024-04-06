clear
len = 24 * 3600 * 20;
k = 0.0001;
h = 200 * 20;

% x = 1:h:len;
% y = zeros(1, length(x));

[t, its] = estimate_degradation(0, k, h, 1000);
printf("Found RUL at %0.1fhr in %i its\n", t / (20*3600), its);

% figure
% plot(x / (20*3600), y * 100)
% hold on
% xlabel("Time (hr)")
% ylabel("Degradation (%)")

% greater = (x)(y>=0.75);
% if length(greater) > 0
%     tau = greater(1);
%     plot([tau tau] / (20*3600) , [0, 1] * 100)
%     printf("True RUL: %.1fhr\n", tau / (20*3600))
% else
%     printf("RUL exceeds specified interval.\n")
% end
