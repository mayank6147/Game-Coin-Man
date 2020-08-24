package com.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;


import java.util.ArrayList;
import java.util.Random;

import sun.rmi.runtime.Log;

public class CoinGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	int manState=0;
	int pause=0;
	int  manY=0;
	float gravity=0.4f;
	float velocity=0;
	ArrayList<Integer> coinX  = new ArrayList<Integer>();
	ArrayList<Integer> coinY  = new ArrayList<Integer>();
	ArrayList<Integer> bombX  = new ArrayList<Integer>();
	ArrayList<Integer> bombY  = new ArrayList<Integer>();
	ArrayList<Rectangle>coinRectangle = new ArrayList<Rectangle>();
	ArrayList<Rectangle>bombRectangle = new ArrayList<Rectangle>();
	Rectangle manRectangle;
	Texture coin;
	int coinCount=0;
	Random random;
	Texture bomb;
	float height;
	int bombCount=0;
	int gameState=0;
	int score=0;
	BitmapFont font;
	Texture dizzy ;
	private Music  music_level1;
	private Music   deadSound;
	private Music  coinCollect;
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		man  = new Texture[4];
		man[0] = new Texture("frame-1.png");
        man[1] = new Texture("frame-2.png");
        man[2] = new Texture("frame-3.png");
        man[3] = new Texture("frame-4.png");
		manY=Gdx.graphics.getHeight() / 2;
		coin = new Texture("coin.png");
		random = new Random();
		bomb = new Texture("bomb.png");
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
		dizzy = new Texture("dizzy-1.png");
		music_level1 = Gdx.audio.newMusic(Gdx.files.internal("level1.ogg"));
		music_level1.setLooping(true);
		music_level1.play();
		deadSound = Gdx.audio.newMusic(Gdx.files.internal("dead.wav"));
		deadSound.setLooping(true);
		coinCollect = Gdx.audio.newMusic(Gdx.files.internal("coinsoundcollision.wav"));
		coinCollect.setLooping(true);

	}
	public void makeCoin(){
		height = random.nextFloat()*Gdx.graphics.getHeight();
		coinY.add((int)height);
		coinX.add(Gdx.graphics.getWidth());
	}
	public void makeBomb(){
		float  pos = random.nextFloat()*Gdx.graphics.getHeight();
		bombY.add((int)pos-2);
		bombX.add(Gdx.graphics.getWidth()-3);
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		/* game state*/
		if(gameState==1){
			// Game is Live
			if(Gdx.input.justTouched()){
				velocity -=10;
			}
			/* for coin show up*/
			if(coinCount<100){
				coinCount++;
			}else{
				coinCount=0;
				makeCoin();
			}
			/*boob show up*/
			if(bombCount < 250){
				bombCount++;
			}else{
				bombCount=0;

				makeBomb();
			}
			coinRectangle.clear();
			coinCollect.stop();
			for(int i=0;i<coinX.size();i++){
				batch.draw(coin, coinX.get(i),coinY.get(i));
				coinX.set(i,coinX.get(i)-4);
				coinRectangle.add(new Rectangle(coinX.get(i),coinY.get(i),coin.getWidth(),coin.getHeight()));
			}
			bombRectangle.clear();
			for(int i=0;i<bombX.size();i++){
				batch.draw(bomb,bombX.get(i),bombY.get(i));
				bombX.set(i,bombX.get(i)-4);
				bombRectangle.add(new Rectangle(bombX.get(i),bombY.get(i),bomb.getWidth(),bomb.getHeight()));
			}
			/*for slowing spped of man*/
			if(pause<8){
				pause++;
			}else{
				pause=0;
				if(manState <3){
					/* changing the man frame*/
					manState++;
				}else{
					manState=0;
				}}
			/* for falling the man*/
			velocity +=gravity;
			manY -=velocity;
			if(manY <=0){
				manY=0;

			}

		}else if(gameState==0){
			// wating to start
			if(Gdx.input.justTouched()){
				gameState=1;
			}

		}else if(gameState ==2){
			// gameover
			if(Gdx.input.justTouched()){
				gameState=1;
				manY=Gdx.graphics.getHeight() / 2;
				coinY.clear();
				coinX.clear();
				bombX.clear();
				bombY.clear();
				bombCount=0;
				coinCount=0;
				bombRectangle.clear();
				coinRectangle.clear();
				velocity=0;
				score=0;
				music_level1.play();
				deadSound.stop();
			}
		}
		/* for background*/

		/* for jummping the man*/
		/*tem.out.println(manY);
		System.out.println(velocity);
		System.out.println(gravity);*/
		if(gameState==2){
			batch.draw(dizzy, Gdx.graphics.getWidth() / 2 - man[manState].getWidth()/2,manY);
		}else{
			batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[manState].getWidth()/2,manY);
		}

		manRectangle = new Rectangle(Gdx.graphics.getWidth() / 2 - man[manState].getWidth()/2,manY,man[manState].getWidth(),man[manState].getHeight());
       /* coin collision*/
        for(int i=0;i<coinRectangle.size();i++){
            if(Intersector.overlaps(coinRectangle.get(i),manRectangle)){
                Gdx.app.log("collision","COOLISIONS");
                score++;
                coinRectangle.remove(i);
                coinX.remove(i);
                coinY.remove(i);
				String s = ""+score;
				Gdx.app.log("Score" , s);

				coinCollect.play();

            }
        }
		music_level1.setVolume(1f);
        /* bomb collosion*/
		for(int i=0;i<bombRectangle.size();i++){
			if(Intersector.overlaps(bombRectangle.get(i),manRectangle)){
				Gdx.app.log("BOMb","COOLISIONSbomb");
					gameState=2;
					music_level1.stop();
					deadSound.play();
			}
		}
		font.draw(batch,String.valueOf(score),100,200);

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();

	}
}
