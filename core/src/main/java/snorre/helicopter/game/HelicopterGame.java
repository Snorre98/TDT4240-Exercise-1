package snorre.helicopter.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.awt.Rectangle;
import java.util.Random;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class HelicopterGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture heliTexture;
    private Sprite heliSprite;
    private FitViewport viewport;

    private boolean movingRight = true;
    private boolean movingUp = true;

    private float currentXSpeed;
    private float currentYSpeedUp;
    private float currentYSpeedDown;

    @Override
    public void create() {
        batch = new SpriteBatch();
        heliTexture = new Texture("heli-sprite.png");
        heliSprite = new Sprite(heliTexture);
        heliSprite.setSize(3,1);
        viewport = new FitViewport(8,8);

        currentXSpeed = setRandomXSpeed();
        currentYSpeedUp = setRandomYSpeedUp();
        currentYSpeedDown = setRandomYSpeedDown();
    }

    @Override
    public void resize(int width, int height){
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        draw();
        logic();
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        heliSprite.draw(batch);
        batch.end();
    }

    private float setRandomXSpeed(){
        float min = 1.5f;
        float max = 2.0f;
        Random r = new Random();
        return min + r.nextFloat() * (max - min);
    }

    private float setRandomYSpeedUp(){
        float min = .25f;
        float max = .50f;
        Random r = new Random();
        return min + r.nextFloat() * (max - min);
    }

    private float setRandomYSpeedDown(){
        float min = 1.75f;
        float max = 2.25f;
        Random r = new Random();
        return min + r.nextFloat() * (max - min);
    }

    private void logic(){
        float delta = Gdx.graphics.getDeltaTime();
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float heliWidth = heliSprite.getWidth();
        float heliHeight = heliSprite.getHeight();


        if(heliSprite.getX() > worldWidth - heliWidth){
            movingRight = false;
            currentXSpeed = setRandomXSpeed();
        }else if(heliSprite.getX() < 0){
            movingRight = true;
            currentXSpeed = setRandomXSpeed();
        }

        if(heliSprite.getY() > worldHeight - heliHeight){
            movingUp = false;
            currentYSpeedDown = setRandomYSpeedDown();
        } else if (heliSprite.getY() < 0) {
            movingUp = true;
            currentYSpeedUp = setRandomYSpeedUp();
        }

        if(movingRight){
            heliSprite.translateX(currentXSpeed*delta);
            heliSprite.setFlip(true, false);
        }else {
            heliSprite.translateX(-currentXSpeed*delta);
            heliSprite.setFlip(false, false);
        }

        if(movingUp){
            heliSprite.translateY(currentYSpeedUp*delta);
        } else {
            heliSprite.translateY(-currentYSpeedDown*delta);
        }

    }

    @Override
    public void dispose() {
        batch.dispose();
        heliTexture.dispose();
    }
}
