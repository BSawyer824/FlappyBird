/*
 *Based on  YouTube Tutorial by Jaryt Bustard  https://www.youtube.com/watch?v=I1qTZaUcFX0
 */
package flappyBird;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.Timer;


public final class FlappyBird implements ActionListener, MouseListener, KeyListener {

    public static FlappyBird flappyBird;
    public final int WIDTH = 1000;
    public final int HEIGHT = 800;
    public Renderer renderer;
    public Random rand;
    public Rectangle bird;
    public int ticks;
    public int yMotion;
    public ArrayList<Rectangle> columns;
    public boolean gameOver = false;
    public boolean started = false;
    public int score = 0;
    
    public FlappyBird() {
        
        JFrame jframe = new JFrame();
        Timer timer = new Timer(20, this);
        renderer = new Renderer();
        rand = new Random();
        
        //Create the Canvas
        jframe.add(renderer);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //terminates program on Red X Close
        jframe.setSize(WIDTH, HEIGHT);
        jframe.addMouseListener(this);
        jframe.addKeyListener(this);
        jframe.setVisible(true);
        jframe.setResizable(false);
        jframe.setTitle("Flappy Red Box");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create Moving Objects
        bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20); //places bird in center of screen
        columns = new ArrayList<>();
        addColumn(true);
        addColumn(true);
        addColumn(true);
        addColumn(true);
            
        timer.start();
        
    }
    
    void repaint(Graphics g) {
        
        //BACKGROUND / SKYE
        g.setColor(Color.CYAN);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        //draw dirt
        g.setColor(Color.orange);
        g.fillRect(0, HEIGHT - 120, WIDTH, 150);
        
        //draw grass
        g.setColor(Color.green);
        g.fillRect(0, HEIGHT - 120, WIDTH, 20);
        
        //draw bird
        g.setColor(Color.red);
        g.fillRect(bird.x, bird.y, bird.width, bird.height);
        
        columns.forEach((column) -> {
            paintColumn(g, column);
        });
        
        //Start Game Message
        if (!started) {
            g.setColor(Color.white);
            g.setFont(new Font("Arial", 1, 100));
            g.drawString("Click to Start!", 100, HEIGHT / 2 - 50);
        }
        
        //Game Over Message
        g.setColor(Color.white);
        g.setFont(new Font("Arial", 1, 100));
        if(gameOver) {
            g.drawString("Game Over!", 100, HEIGHT / 2 - 50);
        }
        
        //Score Keeping Message
        g.setColor(Color.white);
        g.setFont(new Font("Arial", 1, 100));
        if (!gameOver && started) {
            g.drawString(String.valueOf(score), WIDTH / 2 - 25, 100);
        }
        
    }   

    public void paintColumn(Graphics g, Rectangle column) {
        g.setColor(Color.green.darker());
        g.fillRect(column.x, column.y, column.width, column.height);
        
    }
    
    public void addColumn(boolean start){
        int space = 300;
        int width = 100;
        int height = 50 + rand.nextInt(300);
        
        //create new rectangle
        if (start) {
            columns.add(new Rectangle(WIDTH + width + columns.size() * 300, HEIGHT - height - 120, width, height)); 
            columns.add(new Rectangle(WIDTH + width + (columns.size() - 1) * 300, 0, width, HEIGHT - height - space));
        } else {
            columns.add(new Rectangle(columns.get(columns.size() - 1).x + 600, HEIGHT - height - 120, width, height)); 
            columns.add(new Rectangle(columns.get(columns.size() - 1).x, 0, width, HEIGHT - height - space));            
        }
    }
    
    public void jump() {
        //reset game
        if (gameOver) {
            
          //Create Moving Objects
            bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20); //places bird in center of screen
            columns.clear();
            yMotion = 0;
            score = 0;
            
            addColumn(true);
            addColumn(true);
            addColumn(true);
            addColumn(true);
            
            gameOver = false;
        }
        
        if (!started) {
            started = true;
        } else if (!gameOver) {
            if (yMotion > 0) {
                yMotion = 0;
            }
            yMotion -= 10;
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        //runs each time an action is performed by the user
        ticks++;
        int speed = 10;
        
        if (started) {
            //motion of the Columns already painted
            for(int i = 0; i < columns.size(); i++){
                Rectangle column = columns.get(i);
                column.x -= speed;
            }

            //remove columns once off screen
            for(int i = 0; i < columns.size(); i++){
                Rectangle column = columns.get(i);
                if (column.x + column.width < 0) {
                    columns.remove(column);

                    if(column.y == 0) {
                        //only add a column once
                        addColumn(false);
                    }
                }
            }

            //motion of the bird
            if (ticks % 2 == 0 && yMotion < 15) {
                yMotion+=2;
            }
            bird.y += yMotion;

            //check for Game Over conditions
            for (Rectangle column : columns) {
                if(column.y == 0 && bird.x + bird.width / 2 > column.x + column.width / 2 - 10 && bird.x + bird.width / 2 < column.x + column.width / 2 + 10) { //is in the center of the two columns
                    score++;
                }
                if(column.intersects(bird)) {
                    gameOver = true;
                    if (bird.x <= column.x) {
                        bird.x = column.x - bird.width; //column moves dead bird
                    } else {
                        if(column.y != 0) {
                            bird.y = column.y - bird.height;
                        } else if (bird.y < column.height) {
                            bird.y = column.height;
                        }
                    }
                    
                }
            }
            if(bird.y > HEIGHT - 120 || bird.y < 0) {
                
                gameOver = true;
            }
            if (gameOver) {
                bird.y = HEIGHT - 120 - bird.height;
            }
        }
        //Repaint the scene
        renderer.repaint();
    }
    
    public static void main(String[] args) {
        
        flappyBird = new FlappyBird();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        jump();
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE) {
            jump();
        }
    }



    
    
    
}
