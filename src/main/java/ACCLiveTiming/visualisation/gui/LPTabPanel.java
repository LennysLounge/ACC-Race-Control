/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.visualisation.gui;

import ACCLiveTiming.visualisation.LookAndFeel;
import java.util.LinkedList;
import java.util.List;
import static processing.core.PConstants.CENTER;

/**
 *
 * @author Leonard
 */
public class LPTabPanel extends LPContainer {

    private List<LPContainer> tabs = new LinkedList<>();
    private int tabIndex = 0;
    private LPComponent currentTab = null;

    public LPTabPanel() {
    }

    public void addTab(LPContainer tab) {
        tabs.add(tab);
        currentTab = tab;
        tabIndex = tabs.size()-1;
    }

    public void removeTab(LPContainer tab) {
        tabs.remove(tab);
    }

    public void draw() {
        if (tabs.isEmpty()) {
            return;
        }

        int lineHeight = LookAndFeel.get().LINE_HEIGHT;

        applet.textAlign(CENTER, CENTER);
        applet.fill(50);
        applet.noStroke();
        applet.rect(0, 0, applet.width, lineHeight);
        float tabSize = applet.width / tabs.size();
        for (int i = 0; i < tabs.size(); i++) {
            if (i == tabIndex) {
                applet.fill(30);
                applet.rect(i * tabSize, 0, tabSize, lineHeight);
            }
            applet.fill(255);
            applet.text(tabs.get(i).getName(), i * tabSize + tabSize / 2f, lineHeight * 0.5f);
        }
    }

    @Override
    public void mousePressed(int x, int y, int button) {
        if (tabs.isEmpty()) {
            return;
        }
        int lineHeight = LookAndFeel.get().LINE_HEIGHT;
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
        currentTab.setPosition(0, LookAndFeel.get().LINE_HEIGHT);
        currentTab.setSize(getWidth(), getHeight() - LookAndFeel.get().LINE_HEIGHT);
        addComponent(currentTab);
        invalidate();
    }
    
    @Override
    public void onResize(int w, int h){
        currentTab.setPosition(0, LookAndFeel.get().LINE_HEIGHT);
        currentTab.setSize(getWidth(), getHeight() - LookAndFeel.get().LINE_HEIGHT);
    }

}
