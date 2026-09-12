/*
 * ok so, what this does is basically i want to make zoom to center the mouse
 * but that needs access to the viewport and frame, but canvas cannot access 
 * parent container (technically it can, but thats not a good way), so i want
 * to put a function in Frame. but simply putting the function in frame cant be
 * accessed by canvas. this is why i am using interface. this way it can be
 * accessed by both canvas and frame. its written/defined in frame, but because
 * its declared outside frame, it doesnt need to access frame directly
 */
interface ZoomListener{
    void zoomRequested(int mouseX,int mouseY,int rotation);
}
