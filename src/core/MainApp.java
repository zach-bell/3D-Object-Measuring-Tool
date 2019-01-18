package core;

import java.io.File;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;
import processing.event.MouseEvent;

public class MainApp extends PApplet {
	
	public static final String VERSION = "0.1";
	
	// Private objects
	private File currentObject;
	private PShape currentShape;
	private PImage menuIconImage;
	// Private variables
	private int mouseLastX = 0, mouseLastY = 0;
	private float rotateObjectY = 0, rotateCameraX = 0.9f * PI;
	private float mouseScrollFactor = 1;
	
	public void settings() {
		size(1280, 720, P3D);
	}
	
	public void setup() {
		currentObject = null;
		
		surface.setTitle("3D Object Measuring Tool because I don't know what to name it yet.");
		
		// Sets the camera object
		camera(width / 2.0f, height / 2.0f, (height/2.0f) / tan(PI * 30.0f / 180.0f),
				width/2.0f, height/2.0f, 0, 0,1,0);
		
		menuIconImage = loadImage("res/hamburger-menu-icon.png");
		
		//selectInput("Select a 3D model to load:", "fileSelected");
	}
	
	public void draw() {
		background(100);
		lights();
		drawUi(); // needs to be before camera translations
		drawCamera();
		
		if (currentShape != null) {
			scale(3f * mouseScrollFactor, 3f * mouseScrollFactor, 3f * mouseScrollFactor);
			shape(currentShape, 0, 0);
			currentShape.rotateY(rotateObjectY);
		}
	}
	
	// Draws on screen elements
	private void drawUi() {
		
	}
	
	public void drawCamera() {
		translate(width / 2, height / 2);
		rotateX(rotateCameraX);
		getMouseDragging();
	}
	
	public void mouseWheel(MouseEvent event) {
		float e = event.getCount();
		// e > 0 means mouseWheelUp, while e < 0 means mouseWheelDown
		if (e > 0) {
			mouseScrollFactor -= 0.05f;
		} else if (e < 0){
			mouseScrollFactor += 0.05f;
		}
	}
	
	// Mouse dragging method
	private void getMouseDragging() {
		// Will track the mouse inside of the window in the application
		if (!mousePressed & (mouseX > 0) & (mouseY > 0) & (mouseX < width) & (mouseY < height)) {
			mouseLastX = mouseX;
			mouseLastY = mouseY;
		}
		// When pressed will move the translate in appropriate directions from that point of click
		if (mousePressed & (mouseX > 0) & (mouseY > 0) & (mouseX < width) & (mouseY < height)) {
			if (mouseX < (mouseLastX)-5) {
				rotateObjectY = map(mouseX,mouseLastX,(mouseLastX - width),0,0.05f);
			} else if (mouseX > (mouseLastX)+5) {
				rotateObjectY = -map(mouseX,mouseLastX,(mouseLastX + width),0,0.05f);
			}
			if (mouseY < (mouseLastY)-5) {
				rotateCameraX += map(mouseY,mouseLastY,(mouseLastY - width),0,0.08f);
			} else if (mouseY > (mouseLastY)+5) {
				rotateCameraX -= map(mouseY,mouseLastY,(mouseLastY + width),0,0.08f);
			}
		} else {
			rotateObjectY = lerp(0 , rotateObjectY, 0.8f);
		}
	}
	
	public void fileSelected(File selection) {
		if (selection == null) {
			println("File window closed with no selection.");
		} else {
			println("File: " + selection.getName() + " loaded.");
			currentObject = selection;
		}
		if (currentObject != null) {
			try {
				currentShape = loadShape(currentObject.getAbsolutePath());
			} catch (Exception e) {
				println("Boy did something go wrong with that OBJ file.");
				e.printStackTrace();
			}
		}
		if (currentShape == null)
			println("The shape is somehow still null.");
	}
}
