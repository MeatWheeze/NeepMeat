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

% Rate is constant
rate_func = @(health) -k0 * (1500 / 300);
%efficiency = @(health) (exp(health * 4) / (1 + exp(health * 4))) / 2;

% Health is between 1 and 0
health = 1;


dt = 20;
t_end = 20 * 3600 * 0.5;

healths = zeros(1, t_end / dt);
efficiencies = zeros(1, t_end / dt);

i = 1;
t = 0;
while t < t_end
    rate = rate_func(health) * dt;
    health = max(0, health + rate);

    healths(i) = health;
    efficiencies(i) = efficiency(health);

    t = t + dt;
    i = i + 1;
end

figure
hold on
plot([0:dt:t_end - 1] / (20 * 3600), healths)
plot([0:dt:t_end - 1] / (20 * 3600), efficiencies)
legend("Health", "Efficiency")


