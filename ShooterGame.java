import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.stage.Screen;
import javafx.scene.text.TextAlignment;
import javafx.scene.input.MouseButton;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.nio.file.Paths;

public class ShooterGame extends Application {
  // Define lastMouseX and lastMouseY variables
  double lastMouseX = 0;
  double lastMouseY = 0;
  private double backgroundOffset = 0;
  long lastShotTime = 0;
  long cooldownTime = 500; // in milliseconds
  
  @Override
  public void start(Stage primaryStage) {
    // Get the screen dimensions
    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
    // Create the root node
    Group root = new Group();
    // Add a background image
    Image backgroundImage = new Image("arena.jpg");
    ImageView backgroundImageView = new ImageView(backgroundImage);
    backgroundImageView.setFitWidth(screenBounds.getWidth());
    backgroundImageView.setFitHeight(screenBounds.getHeight());
    root.getChildren().add(backgroundImageView);
    
    // Create the start screen
    Text title = new Text("Survive!");
    title.setFont(Font.font("Verdana", FontWeight.BOLD, 60));
    title.setX(screenBounds.getWidth()/3);
    title.setY(screenBounds.getHeight()/2.5);
    title.setTextAlignment(TextAlignment.CENTER);
    title.setFill(Color.RED); // Set the text color to red
    
    
    Text instructions = new Text("Press SPACE to start the game");
    instructions.setFont(Font.font("Verdana", FontWeight.NORMAL, 30));
    instructions.setX(screenBounds.getWidth()/3);
    instructions.setY(screenBounds.getHeight()/1.5);
    instructions.setTextAlignment(TextAlignment.CENTER);
    instructions.setFill(Color.RED);
    
    // Add the title and instructions to the root node
    root.getChildren().addAll(title, instructions);
    
    // Create the scene and add the root node to it
    Scene startScene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
    
    
    // Add an event handler to start the game when SPACE is pressed
    startScene.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.SPACE) {
        // Switch to the game scene
        primaryStage.setScene(createGameScene());
      }
    });
    
    // Set the start scene to the stage and show the stage
    primaryStage.setScene(startScene);
    primaryStage.show();
  }
  private Scene createGameScene() {
    
    // Get the screen dimensions
    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
    // Create the root node
    Group root = new Group();
    

    
    // Create a player
    Image playerImage = new Image("player.png");
    ImageView player = new ImageView(playerImage);
    player.setFitWidth(150);
    player.setFitHeight(75);
    player.setX(50);
    player.setY(250);
    
    
    // Add a background image
    Image backgroundImage = new Image("arena.jpg");
    ImageView backgroundImageView1 = new ImageView(backgroundImage);
    backgroundImageView1.setFitWidth(screenBounds.getWidth());
    backgroundImageView1.setFitHeight(screenBounds.getHeight());
    root.getChildren().add(backgroundImageView1);
    
    // Create a bullet
    Image bulletImage = new Image("bullet.png");
    
    // Add the player to the root node
    root.getChildren().add(player);
    
    // Create the scene and add the root node to it
    Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
    List<ImageView> bullets = new ArrayList<>();
    List<ImageView> bulletsToRemove = new ArrayList<>();
    
    
    // Add event handlers to move the player with mouse
    scene.setOnMouseMoved(event -> {
      double mouseX = event.getX();
      double mouseY = event.getY();
      double playerX = player.getX() + (player.getFitWidth() / 2);
      double playerY = player.getY() + (player.getFitHeight() / 2);
      double angle = Math.atan2(mouseY - playerY, mouseX - playerX) * (180 / Math.PI);
      player.setRotate(angle);
      player.setY(mouseY - (player.getFitHeight() / 2));
      player.setX(mouseX - (player.getFitWidth() / 2));
    });
    
    // Add a game loop to update the player's position
    Timeline gameLoop = new Timeline(new KeyFrame(Duration.millis(20), event -> {
      // Hide the mouse cursor
      scene.setCursor(Cursor.NONE);
      
      // Calculate the distance the mouse moved since the last frame
      double deltaX = scene.getWidth() / 2 - lastMouseX;
      double deltaY = scene.getHeight() / 2 - lastMouseY;
      
      // Move the player in the direction of the mouse
      player.setX(player.getX() + deltaX);
      player.setY(player.getY() + deltaY);
      
      // Save the current mouse position
      lastMouseX = scene.getWidth() / 2;
      lastMouseY = scene.getHeight() / 2;
      
      
      
    }));
    
    gameLoop.setCycleCount(Timeline.INDEFINITE);
    gameLoop.play();
    
    // Add event handler to shoot bullet when left clicking the mouse
    scene.setOnMouseClicked(event -> {  
      if (event.getButton() == MouseButton.PRIMARY) {
        // Check if enough time has elapsed since the last shot
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime < cooldownTime) {
          return;
        }
            // Load the sound file
        String soundFile = "gunshot.mp3";
        Media sound = new Media(Paths.get(soundFile).toUri().toString());

        // Create a media player to play the sound
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        // Play the gunshot sound effect
        mediaPlayer.play();
        // Update the last shot time
        lastShotTime = currentTime;
        // Calculate the velocity of the bullet based on the angle of the player
        double velocity = 10; // adjust as needed
        double bulletX = player.getX() + (player.getFitWidth() / 2);
        double bulletY = player.getY() + (player.getFitHeight() / 2);
        double angle = player.getRotate() * Math.PI / 180;
        double deltaX = velocity * Math.cos(angle);
        double deltaY = velocity * Math.sin(angle);
        // Create a new bullet
        ImageView bullet = new ImageView(bulletImage);
        bullet.setFitWidth(15);
        bullet.setFitHeight(15);
        bullet.setX(bulletX);
        bullet.setY(bulletY);
        bullet.setRotate(player.getRotate());
        root.getChildren().add(bullet);
        // Add the bullet to the bullets list
        bullets.add(bullet);
        
        
        // Add a game loop to update the position of the bullet
        Timeline bulletLoop = new Timeline(new KeyFrame(Duration.millis(20), e -> {
          // Move the bullet
          bullet.setX(bullet.getX() + deltaX);
          bullet.setY(bullet.getY() + deltaY);
          
          // Check if the bullet is out of bounds
          if (bullet.getX() < 0 || bullet.getX() > screenBounds.getWidth() || 
              bullet.getY() < 0 || bullet.getY() > screenBounds.getHeight()) {
            bulletsToRemove.add(bullet);
          }
          
          // Check for collisions with enemies (if any)
          // ...
        }));
        bulletLoop.setCycleCount(Timeline.INDEFINITE);
        bulletLoop.play();
      }
    });
    
    // Create an enemy
    Image enemyImage = new Image("enemy.png");
    ImageView enemy = new ImageView(enemyImage);
    enemy.setFitWidth(50);
    enemy.setFitHeight(75);
    enemy.setX(screenBounds.getWidth() - 150);
    enemy.setY(250);
    root.getChildren().add(enemy);
    
// Add a game loop to move the enemy towards the player
    Timeline enemyLoop = new Timeline(new KeyFrame(Duration.millis(20), event -> {
      double playerX = player.getX() + (player.getFitWidth() / 2);
      double playerY = player.getY() + (player.getFitHeight() / 2);
      double enemyX = enemy.getX() + (enemy.getFitWidth() / 2);
      double enemyY = enemy.getY() + (enemy.getFitHeight() / 2);
      double angle = Math.atan2(playerY - enemyY, playerX - enemyX) * (180 / Math.PI);
      enemy.setRotate(angle);
      double speed = 2.0;
      double dx = speed * Math.cos(angle * (Math.PI / 180));
      double dy = speed * Math.sin(angle * (Math.PI / 180));
      enemy.setX(enemy.getX() + dx);
      enemy.setY(enemy.getY() + dy);
      
      for (ImageView bullet : bullets) {
        if (bullet.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
          // Remove the bullet and the enemy
          bulletsToRemove.add(bullet);
          root.getChildren().remove(bullet);
          root.getChildren().remove(enemy);
        }
      }
    }));
    enemyLoop.setCycleCount(Timeline.INDEFINITE);
    enemyLoop.play();
    
    
    
    
    return scene;
  }
  
  public static void main(String[] args) {
    launch(args);
  }
}