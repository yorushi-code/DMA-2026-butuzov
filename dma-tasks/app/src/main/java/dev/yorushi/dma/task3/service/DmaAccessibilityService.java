package dev.yorushi.dma.task3.service;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

/** P38: accessibility service configured by @xml/accessibility_service_config. */
public final class DmaAccessibilityService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d("DmaAccessibility", "event " + AccessibilityEvent.eventTypeToString(event.getEventType()));
    }

    @Override
    public void onInterrupt() {
    }
}
