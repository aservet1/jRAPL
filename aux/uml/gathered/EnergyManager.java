package jRAPL;
public class EnergyManager
{
private boolean active = false;
private static boolean libraryLoaded = false;
private static int energyManagersActive = 0;
native static void profileInit();
native static void profileDealloc();
static void loadNativeLibrary()
public void activate()
public void deactivate()
}
