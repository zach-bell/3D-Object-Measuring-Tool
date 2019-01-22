package core;

import java.io.File;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;
import processing.event.MouseEvent;

public class MainApp extends PApplet {
	
	public static final String VERSION = "0.1";
	
	// Private objects
	private File currentFile;
	private PShape currentShape;
	private PImage menuIconImage;
	// Private variables
	private float uiWidth = 280, uiHeight = 300, uiTextPadding = 10;
	private int mouseLastX = 0, mouseLastY = 0;
	private float mouseWheel = 0;
	private boolean menuToggle = true;
	private boolean devMenuToggle = false;
	// camera variables
	private PVector camPos, camLookAt, camUp;
	private float fovy = PI / 2.0f, nearClippingPlaneDistance = 0;
	private float rotateObjectY = 0, rotateCameraX = 0.9f * PI;
	private float mouseScrollFactor = 1;
	
	public void settings() {
		size(1280, 720, P3D);
	}
	
	public void setup() {
		currentFile = null;
		
		surface.setTitle("VisuPoint");
		
		// Sets the camera variables
		camPos = new PVector(width / 2.0f, height / 2.0f, (height/2.0f) / tan(PI * 30.0f / 180.0f));
		camLookAt = new PVector(width/2.0f, height/2.0f, 0);
		camUp = new PVector(0, 1, 0);
		
		camera(camPos.x, camPos.y, camPos.z, camLookAt.x, camLookAt.y, camLookAt.z, camUp.x, camUp.y, camUp.z);
		
		menuIconImage = loadImage("res/hamburger-menu-icon.png");
	}
	
	// Main draw loop
	public void draw() {
		background(100);
		lights();
		drawUi(); // needs to be before camera translations
		drawCamera();
		
		if (currentShape != null) {
			scale(4f * mouseScrollFactor, 4f * mouseScrollFactor, 4f * mouseScrollFactor);
			shape(currentShape, 0, 0);
			currentShape.rotateY(rotateObjectY);
		}
	}
	
	// Draws on screen elements
	private void drawUi() {
		noStroke();
		if (menuToggle) {
			fill(35);
			rect(0, 0, uiWidth, uiHeight, 0, 0, 12, 0);
			drawText();
		}
		fill(200);
		image(menuIconImage, 0, 0, 50, 50);
		
		if (devMenuToggle) {
			drawDevUi();
		}
	}
	
	// Draws text elements on the screen
	private void drawText() {
		textSize(34);
		fill(230);
		text("Open New File", uiTextPadding, 100);
		// File information
		textSize(24);
		if (currentFile != null) {
			fill(200);
			text("" + currentFile.getName(), uiTextPadding, 130);
		}
		// Program information
		fill(180);
		text("Version: " + VERSION, uiTextPadding, uiHeight - 60);
		text("Created by:", uiTextPadding, uiHeight - 35);
		text("Zachary Vanscoit", uiTextPadding, uiHeight - 10);
	}
	
	// Draws the dev UI
	private void drawDevUi() {
		fill(35);
		rect(width - uiWidth, 0, uiWidth, height);
		if (mousePressed) {
			noFill();
			stroke(100, 255, 255);
			rect(mouseLastX - 5, mouseLastY - 5, 10, 10);
			line(mouseLastX, mouseLastY, mouseX, mouseY);
			noStroke();
		}
		drawDevText();
	}
	
	// Draws text about variables in the program for dev purposes
	private void drawDevText() {
		// Headers
		fill(255, 100, 100);
		textSize(30);
		text("Mouse Vars", (width - uiWidth) + uiTextPadding, 32);
		text("Camera Vars", (width - uiWidth) + uiTextPadding, 200);
		text("Object vars", (width - uiWidth) + uiTextPadding, 300);
		
		// Vars
		fill(100, 255, 255);
		textSize(20);
		if (currentFile != null)
			text("" + currentFile.getAbsolutePath(), uiTextPadding, height - uiTextPadding);
		// Mouse vars
		text("Mouse X: " + mouseX, (width - uiWidth) + uiTextPadding, 64);
		text("Mouse Y: " + mouseY, (width - uiWidth) + uiTextPadding, 88);
		text("Mouse Pressed: " + mousePressed, (width - uiWidth) + uiTextPadding, 112);
		text("Mouse wheel: " + nf(mouseWheel, 1, 2), (width - uiWidth) + uiTextPadding, 134);
		// Camera Vars
		text("Camera Rotate X: " + nf(rotateCameraX, 2, 3), (width - uiWidth) + uiTextPadding, 224);
		// Object Vars
		if (currentShape != null) {
			text("Object Rotation Y: " + nf(rotateObjectY, 1, 3), (width - uiWidth) + uiTextPadding, 324);
		}
	}
	
	// Draws the camera and translates it appropriately
	private void drawCamera() {
		translate(width / 2, height / 2);
		rotateX(rotateCameraX);
		getMouseDragging();
	}
	
	private void rayPicking() {
		int x = mouseX, y = mouseY;
		PVector view = PVector.sub(camLookAt, camPos);
		view = view.normalize();
		PVector h = view.cross(camUp);
		h = h.normalize();
		PVector v = h.cross(view);
		v = v.normalize();
		
		// Sets 
		float vLength = tan(fovy / 2) * nearClippingPlaneDistance;
		float hLength = vLength * (width / height);
		
		
		// translates mouse coordinates to center
		x -= width / 2;
		y -= height /2;
		// scale mouse coordinates to half view port w/h
		x /= (width / 2);
		y /= (height / 2);
		
		// linear comb of intersection of picking ray with view port plane
		PVector pos = new PVector(camPos.x + view.x * nearClippingPlaneDistance + h.x * x + v.x * y,
				camPos.y + view.y * nearClippingPlaneDistance + h.y * x + v.y * y,
				camPos.z + view.z * nearClippingPlaneDistance + h.z * x + v.z * y);
		PVector dir = PVector.sub(pos, camPos);
	}
	
	// Listens for a mouse click
	public void mouseClicked() {
		if ((mouseX > 0) & (mouseY > 0) & (mouseX < 50) & (mouseY < 50))
			menuToggle = !menuToggle;
		if (menuToggle & ((mouseX < width) & (mouseY < height) & (mouseX > uiWidth) | (mouseY > uiHeight)))
			if (currentFile != null)
				menuToggle = false;
		
		if (menuToggle) {
			// OpenFile button location
			if ((mouseX > 10) & (mouseY > 70) & (mouseX < 250) & (mouseY < 100))
				handleFile();
		}
	}
	
	// Listens for the mouse wheel to scroll the camera
	public void mouseWheel(MouseEvent event) {
		mouseWheel = event.getCount();
		// e > 0 means mouseWheelUp, while e < 0 means mouseWheelDown
		if (mouseWheel > 0) {
			mouseScrollFactor -= 0.07f;
		} else if (mouseWheel < 0){
			mouseScrollFactor += 0.07f;
		}
	}
	
	public void keyPressed() {
		println("Key pressed: " + key);
		if (key == '1') {
			devMenuToggle = !devMenuToggle;
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
			rotateCameraX = constrain(rotateCameraX, (PI / 2), ((3 * PI) / 2));
		} else {
			rotateObjectY = lerp(0 , rotateObjectY, 0.8f);
		}
	}
	
	// Handles the file and returns to the top of the method
	private void handleFile() {
		selectInput("Select a 3D model to load:", "fileSelected");
	}
	
	// Is called by the selectInput Method
	public void fileSelected(File selection) {
		if (selection == null) {
			println("File window closed with no selection.");
		} else {
			println("File: " + selection.getName() + " loaded.");
			currentFile = selection;
		}
		if (currentFile != null) {
			try {
				currentShape = loadShape(currentFile.getAbsolutePath());
			} catch (Exception e) {
				println("Boy did something go wrong with that OBJ file.");
				e.printStackTrace();
			}
		}
		if (currentShape == null)
			println("The shape is somehow still null.");
		menuToggle = false;
	}
}
