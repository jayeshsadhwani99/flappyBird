package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

import javax.swing.plaf.TextUI;
import javax.xml.soap.Text;

import jdk.nashorn.internal.ir.WhileNode;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	int score = 0;
	int scoringTube = 0;
	BitmapFont font;
	Texture gameOver;

	//ShapeRenderer shapeRenderer;
	//Test the GameState(GameOver)
	int gameState = 0;
	/*==================================Background texture==========================================*/
	Texture background;

	/*====================================Bird variables============================================*/
	Texture[] birds;
	int birdState = 0;
	float birdY;
	int pause = 0;
	int velocity = 0;
	float gravity = 2;
	Circle birdCircle;

	/*=========================================Tubes================================================*/
	Texture topTube;
	Texture bottomTube;
	float gap = 400;
	float maxTubeOffset;
	Random randomGenerator;
	float tubeVelocity = 4;
	int numberOfTubes = 4;
	float[] tubeX = new float[numberOfTubes];
	float[] tubeOffset = new float[numberOfTubes];
	float distanceBetweenTubes;
	Rectangle[] topTubesRectanges;
	Rectangle[] bottomTubesRectanges;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
//		shapeRenderer = new ShapeRenderer();
		birdCircle = new Circle();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
		gameOver = new Texture("gameover.png");
		/*=====================================Background===========================================*/
		background = new Texture("bg.png");

		/*========================================Bird==============================================*/
		birds = new Texture[2];
		//image1
		birds[0] = new Texture("bird.png");
		//image2
		birds[1] = new Texture("bird2.png");

		/*========================================Tubes=============================================*/
		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth() * 3/4;
		topTubesRectanges = new Rectangle[numberOfTubes];
		bottomTubesRectanges = new Rectangle[numberOfTubes];

		startGame();
	}

	public void startGame() {
		birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;
		for(int i = 0; i < numberOfTubes ; i++) {
			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
			tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;
			topTubesRectanges[i] = new Rectangle();
			bottomTubesRectanges[i] = new Rectangle();
		}
	}

	@Override
	public void render () {

		/*====================================Start=================================================*/
		batch.begin();
		/*==================================Background==============================================*/
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		/*=================================Initialize===============================================*/
		//The bird would be just standing at the start unless the gameState changes to 1
		if(gameState == 1) {
			//The bird starts to fall
			if(Gdx.input.justTouched()) {
				velocity = -30;
			}
			//The bird doesn't fall of the screen
			if(birdY > 0) {
				//Create a jump
				velocity += gravity;
				//Update the position of the bird
				birdY -= velocity;
			} else {
				gameState = 2;
			}

			/*===============================Tubes=====================================*/

			for(int i = 0; i < numberOfTubes ; i++) {

				if(tubeX[i] < -topTube.getWidth()) {
					tubeX[i] += numberOfTubes * distanceBetweenTubes;
					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
				} else {
					tubeX[i] = tubeX[i] - tubeVelocity;

					if(tubeX[scoringTube] < Gdx.graphics.getWidth()) {
						score++;
						Gdx.app.log("Score", Integer.toString(score));
						if(scoringTube < numberOfTubes - 1) {
							scoringTube++;
						} else {
							scoringTube = 0;
						}
					}
				}

				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

				topTubesRectanges[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
				bottomTubesRectanges[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());


			}

		//Touch to Start the game
		} else if(gameState == 0) {
			if(Gdx.input.justTouched()) {
				gameState = 1;
			}
		} else {
			batch.draw(gameOver, Gdx.graphics.getWidth() / 2 - gameOver.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameOver.getHeight() / 2);
			if(Gdx.input.justTouched()) {
				gameState = 1;
				startGame();
				score = 0;
				scoringTube = 0;
				velocity = 0;
			}
		}

		/*=======================================Bird===============================================*/
		//To set the frequency of flap
		if(pause < 5) {
			pause++;
		} else {
			pause = 0;
			//To set the flapState
			if(birdState == 0) {
				birdState = 1;
			} else {
				birdState = 0;
			}
		}
		//Draw the Bird
		batch.draw(birds[birdState], Gdx.graphics.getWidth() / 2 - birds[0].getWidth() / 2, birdY);

		font.draw(batch, Integer.toString(score), 100, 200);

		/*========================================End===============================================*/
		batch.end();

		/*=====================================Collision============================================*/

		birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[0].getHeight() / 2, birds[0].getWidth() / 2);

//		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//		shapeRenderer.setColor(Color.RED);
//		shapeRenderer.circle(birdCircle.x,birdCircle.y, birdCircle.radius);

		for(int i = 0; i < numberOfTubes ; i++) {
//			shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
//			shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());

			if(Intersector.overlaps(birdCircle, topTubesRectanges[i]) || Intersector.overlaps(birdCircle, bottomTubesRectanges[i])) {

				gameState = 2;
			}

		}

//		shapeRenderer.end();
	}
}
