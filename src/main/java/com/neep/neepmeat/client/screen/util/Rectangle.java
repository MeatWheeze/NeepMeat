package com.neep.neepmeat.client.screen.util;

public interface Rectangle
{
    int x();

    int y();

    int w();

    int h();

    default Rectangle offset(int dx, int dy)
    {
        return new Immutable(x() + dx, y() + dy, w(), h());
    }

    default boolean isWithin(double mx, double my)
    {
        return mx >= x() && mx <= x() + w()
                && my >= y() && my <= y() + h();
    }

    class Immutable implements Rectangle
    {
        public final int x, y, w, h;

        public Immutable(Rectangle rectangle)
        {
            this.x = rectangle.x();
            this.y = rectangle.y();
            this.w = rectangle.w();
            this.h = rectangle.h();
        }

        public Immutable(int x, int y, int w, int h)
        {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        @Override
        public int x()
        {
            return x;
        }

        @Override
        public int y()
        {
            return y;
        }

        @Override
        public int w()
        {
            return w;
        }

        @Override
        public int h()
        {
            return h;
        }
    }

    class Mutable implements Rectangle
    {
        private int x;
        private int y;

        public Mutable set(int x, int y, int w, int h)
        {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            return this;
        }


        public Mutable setX(int x)
        {
            this.x = x;
            return this;
        }

        public Mutable setY(int y)
        {
            this.y = y;
            return this;
        }

        public Mutable setW(int w)
        {
            this.w = w;
            return this;
        }

        public Mutable setH(int h)
        {
            this.h = h;
            return this;
        }

        private int w;
        private int h;

        public Mutable(Rectangle rectangle)
        {
            this.x = rectangle.x();
            this.y = rectangle.y();
            this.w = rectangle.w();
            this.h = rectangle.h();
        }

        public Mutable(int x, int y, int w, int h)
        {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        @Override
        public int x()
        {
            return x;
        }

        @Override
        public int y()
        {
            return y;
        }

        @Override
        public int w()
        {
            return w;
        }

        @Override
        public int h()
        {
            return h;
        }
    }
}
