package com.example.flappybird;

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

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture end;
	BitmapFont font;
	//ShapeRenderer shapeRenderer;

	Texture birds[];
	int flapState = 0;
	float birdX, birdY;
	int gameState = 0;
	float velocity = 0;
	float gravity = 1.5f;
	Circle birdCircle;

	Texture topTube;
	Texture bottomTube;
	float gap = 400;
	Random random;
	float maxOffset;
	float tubeVelocity = 4;
	int numberOfTubes = 4;
	float tubeX[] = new float[numberOfTubes];
	float tubeOffset[] = new float[numberOfTubes];
	float distanceBetweenTubes;
	Rectangle[] topTubeRectangles = new Rectangle[numberOfTubes];
	Rectangle[] bottomTubeRectangles = new Rectangle[numberOfTubes];

	int score = 0;
	int scoringTube = 0;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
		end = new Texture("gameover.png");
		//shapeRenderer = new ShapeRenderer();

		background = new Texture("bg.png");
		birds = new Texture[20];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");
		birdCircle = new Circle();
		birdX = (Gdx.graphics.getWidth()/2)-birds[0].getWidth()/2;
		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		random = new Random();
		maxOffset = Gdx.graphics.getHeight()/2 - gap/2 - 450;
        distanceBetweenTubes = Gdx.graphics.getWidth() * 3/4;

        startGame();

	}

	public void startGame()
	{
		birdY = (Gdx.graphics.getHeight()/2)-birds[0].getHeight()/2;

		for(int i=0;i<numberOfTubes;i++)
		{
			tubeOffset[i] = (random.nextFloat()-0.5f)*(Gdx.graphics.getHeight() - gap -900);
			tubeX[i] = (Gdx.graphics.getWidth()/2)-topTube.getWidth()/2 + Gdx.graphics.getWidth() + i*distanceBetweenTubes;
			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
		}
		score = 0;
		velocity = 0;
		scoringTube = 0;
	}

	@Override
	public void render () {

		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		if(gameState==1)
		{
			if(tubeX[scoringTube]<Gdx.graphics.getWidth()/2)
			{
				score++;
				if(scoringTube<numberOfTubes-1)scoringTube++;
				else scoringTube=0;
			}


			if(Gdx.input.justTouched())
			{
				velocity = -25f;
			}
			for(int i=0;i<numberOfTubes;i++) {
				if(tubeX[i] < -topTube.getWidth())
				{
					tubeX[i] += numberOfTubes * distanceBetweenTubes;
					tubeOffset[i] = (random.nextFloat()-0.5f)*(Gdx.graphics.getHeight() - gap -400);
				}
				else tubeX[i] = tubeX[i] - tubeVelocity;
				batch.draw(topTube, tubeX[i], (Gdx.graphics.getHeight() / 2) + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], ((Gdx.graphics.getHeight() / 2) - gap / 2 - bottomTube.getHeight()) + tubeOffset[i]);
				topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i],topTube.getWidth(),topTube.getHeight());
				bottomTubeRectangles[i] = new Rectangle(tubeX[i], (Gdx.graphics.getHeight() / 2) - gap / 2 - bottomTube.getHeight() + tubeOffset[i],bottomTube.getWidth(),bottomTube.getHeight());
			}
			if(birdY>0)
			{velocity = velocity + gravity;
			birdY -= velocity;}
			else{
				gameState = 2;
			}
		}
		else if (gameState==0)
		{
			if(Gdx.input.justTouched())
			{
				gameState = 1;
			}
		}
		else if (gameState==2)
		{
			batch.draw(end,Gdx.graphics.getWidth()/2 - end.getWidth()/2, Gdx.graphics.getHeight()/2 - end.getHeight()/2);
			if(Gdx.input.justTouched())
			{
				gameState=1;
				startGame();
			}
		}


		if(flapState == 0)flapState=1;
		else flapState=0;

		batch.draw(birds[flapState],birdX,birdY);
		font.draw(batch,String.valueOf(score),100,200);
		batch.end();



		birdCircle.set(Gdx.graphics.getWidth()/2, birdY+birds[flapState].getHeight()/2,birds[flapState].getWidth()/2);

		/*shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(Color.RED);
		shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);*/
		for(int i=0;i<numberOfTubes;i++)
		{
			//shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i],topTube.getWidth(),topTube.getHeight());
			//shapeRenderer.rect(tubeX[i], (Gdx.graphics.getHeight() / 2) - gap / 2 - bottomTube.getHeight() + tubeOffset[i],bottomTube.getWidth(),bottomTube.getHeight());
			if(Intersector.overlaps(birdCircle,topTubeRectangles[i]) || Intersector.overlaps(birdCircle,bottomTubeRectangles[i])){
				gameState=2;
			}
		}
		//shapeRenderer.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
		birds[0].dispose();
		birds[1].dispose();
		bottomTube.dispose();
		topTube.dispose();
	}
}
