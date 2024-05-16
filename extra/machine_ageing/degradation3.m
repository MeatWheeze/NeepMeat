clear

function e = efficiency(health)
    e = 1 - (1 - health) ^ 4;

    % if health == 0
    %     e = 0;
    % elseif health == 1
    %     e = 1;
    % elseif health < 0.5
    %     e = 2 ^ (20 * health - 10) / 2;
    % else 
    %     e = (2 - 2 ^ (-20 * health + 10)) / 2;
    % end
end

% In this model, the machine ages linearly. Performance degradation will be non-linearly related to health.

k0 = 0.00002;
repair = 0.0015;

% Rate is constant
rate_func = @(health) -k0 * (1500 / 300) * health^0.2;
% rate_func = @(health) -k0 * (1500 / 300);
%efficiency = @(health) (exp(health * 4) / (1 + exp(health * 4))) / 2;

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
plot([0:dt:t_end - 1] / 3600, healths)
plot([0:dt:t_end - 1] / 3600, efficiencies)
legend("Health", "Efficiency")


