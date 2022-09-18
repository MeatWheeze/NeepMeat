package com.neep.neepmeat.client.screen.tablet;

import com.neep.neepmeat.guide.GuideNode;

import java.util.Deque;

public interface ITabletScreen
{
    void setLeftPane(ContentPane element);
    void setRightPane(ContentPane element);
    void push(GuideNode node);
    GuideNode pop();
    Deque<GuideNode> getPath();

    int getAnimationTicks();
}
