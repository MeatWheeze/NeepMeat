function [tau, its] = estimate_degradation(y0, k, h, max_its)
    i = 0;
    x = 0;
    y = y0;
    while i < max_its
        if (i > 1)
            rate = k * 1500 / 300 * (1 - y)^4;
            y = y + h * rate;
            if y >= 0.75
                tau = x;
                its = i;
                return
                % printf("Found RUL at %0.1fhr in %i its\n", x / (20*3600), i);
                break
            end
        end

        x = x + h;
        i = i + 1;
    end
    tau = -1;
    its = max_its;
    return
end
