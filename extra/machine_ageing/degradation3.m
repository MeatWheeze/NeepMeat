clear

function e = efficiency(health)
    e = 1 - (1 - health) ^ 4;
end

% In this model, the machine ages linearly. Performance degradation will be non-linearly related to health.

k0 = 0.00002;
repair = 0.00015;
% repair = 0;

k1 = k0 * (1500 / 300);
% rate_func = @(health) -k0 * (1500 / 300) * health^0.2;
rate_func = @(health) -k1 * health; % No exponent
% rate_func = @(health) -k0 * (1500 / 300); % Linear

c = -log(abs(k1 - repair));
h_ode = @(t) (1 / k1) * (exp(-k1 * t - c) + repair);

% Health is between 1 and 0
health = 1;

dt = 20;
t_end = 24 * 3600;

healths = zeros(1, t_end / dt);
healths2 = zeros(1, t_end / dt);
efficiencies = zeros(1, t_end / dt);

i = 1;
t = 0;
while t < t_end
    rate = rate_func(health) * dt;
    health = min(1, max(0, health + rate + repair));

    healths(i) = health;
    efficiencies(i) = efficiency(health);

    t = t + dt;
    i = i + 1;
end

figure
hold on
time = [0:dt:t_end - dt];
plot(time / 3600, healths)
plot(time / 3600, efficiencies)
healths_ode = h_ode(time);
plot(time / 3600, h_ode([0:dt:t_end - 1]))
legend("Health", "Efficiency", "Analytical Health")


