function [tau, its] = estimate_degradation(y0, h, max_its, rate_func)
    target = 0.75;
    i = 0;
    x = 0;
    y = y0;
    h1 = h;
    while i < max_its
        if (i > 1)
            rate = rate_func(y);
            diff = max((target - y) / target, 0.01);
            h1 = diff * h;
            printf("%f\n", h1);

            y = min(1, y + h1 * rate);
            if y >= target
                tau = x;
                its = i;
                return
                % printf("Found RUL at %0.1fhr in %i its\n", x / (20*3600), i);
            end
        end

        x = x + h1;
        i = i + 1;
    end
    tau = -1;
    its = max_its;
    return
end
