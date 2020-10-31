/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.monitor.visualisation.gui;

import ACCLiveTiming.monitor.visualisation.LookAndFeel;
import java.util.LinkedList;
import java.util.List;
import static processing.core.PConstants.CENTER;

/**
 *
 * @author Leonard
 */
public class LPTabPanel extends LPContainer {

    /**
     * List of tabs.
     */
    private List<LPContainer> tabs = new LinkedList<>();
    /**
     * The index of the currently displayed panel.
     */
    private int tabIndex = 0;
    /**
     * The currently displayed tab.
     */
    private LPComponent currentTab = null;
    /**
     * The tab index the mouse is over.
     */
    private int mouseOverTab = -1;

    public LPTabPanel() {
    }

    public void addTab(LPContainer tab) {
        tabs.add(tab);
        currentTab = tab;
        tabIndex = tabs.size() - 1;
    }

    public void removeTab(LPContainer tab) {
        tabs.remove(tab);
    }

    @Override
    public void draw() {
        if (tabs.isEmpty()) {
            return;
        }

        int lineHeight = LookAndFeel.LINE_HEIGHT;

        applet.textAlign(CENTER, CENTER);
        applet.fill(LookAndFeel.COLOR_DARK_DARK_GRAY);
        applet.noStroke();
        applet.rect(0, 0, applet.width, lineHeight);
        float tabSize = applet.width / tabs.size();
        for (int i = 0; i < tabs.size(); i++) {
            if (i == tabIndex) {
                applet.fill(LookAndFeel.COLOR_MEDIUM_DARK_GRAY);
                applet.rect(i * tabSize, 0, tabSize, lineHeight);
            }
            if(i == mouseOverTab){
                applet.fill(LookAndFeel.TRANSPARENT_WHITE);
                applet.rect(i * tabSize, 0, tabSize, lineHeight);
            }
            applet.fill(255);
            applet.text(tabs.get(i).getName(), i * tabSize + tabSize / 2f, lineHeight * 0.5f);
        }
    }
    
    @Override
    public void onMouseMove(int x, int y){
        if (tabs.isEmpty()) {
            mouseOverTab = -1;
            return;
        }
        if (y > 0 && y < LookAndFeel.LINE_HEIGHT) {
            float tabSize = applet.width / tabs.size();
            int clickedIndex = (int) ((x - (x % tabSize)) / tabSize);
            mouseOverTab = clickedIndex;
        }else{
            mouseOverTab = -1;
        }
    }
    
    @Override
    public void onMouseLeave(){
        mouseOverTab = -1;
    }

    @Override
    public void mousePressed(int x, int y, int button) {
        if (tabs.isEmpty()) {
            return;
        }
        int lineHeight = LookAndFeel.LINE_HEIGHT;
        if (y > 0 && y < lineHeight) {
            float tabSize = applet.width / tabs.size();
            int clickedIndex = (int) ((x - (x % tabSize)) / tabSize);
            setTabIndex(clickedIndex);
        }
    }

    public void setTabIndex(int index) {
        if (index < 0 || index >= tabs.size()) {
            return;
        }
        tabIndex = index;
        if (currentTab != null) {
            removeComponent(currentTab);
        }
        currentTab = tabs.get(tabIndex);
        currentTab.setPosition(0, LookAndFeel.LINE_HEIGHT);
        currentTab.setSize(getWidth(), getHeight() - LookAndFeel.LINE_HEIGHT);
        addComponent(currentTab);
        invalidate();
    }

    @Override
    public void onResize(int w, int h) {
        currentTab.setPosition(0, LookAndFeel.LINE_HEIGHT);
        currentTab.setSize(getWidth(), getHeight() - LookAndFeel.LINE_HEIGHT);
    }

}
