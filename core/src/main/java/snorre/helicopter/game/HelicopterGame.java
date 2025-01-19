package snorre.helicopter.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
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
        input();
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

    private boolean isControlled(){
        return Gdx.input.isKeyPressed(Input.Keys.RIGHT)
            || Gdx.input.isKeyPressed(Input.Keys.LEFT)
            || Gdx.input.isKeyPressed(Input.Keys.UP)
            || Gdx.input.isKeyPressed(Input.Keys.DOWN);
    }

    private void input(){
        float speed = 3.5f;
        float delta = Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            heliSprite.translateX(speed*delta);
            movingRight = true;
            heliSprite.setFlip(true, false);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            heliSprite.translateX(-speed*delta);
            heliSprite.setFlip(false, false);
            movingRight = false;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            heliSprite.translateY(speed*delta*0.75f);
            movingUp = true;
        }else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            heliSprite.translateY(-speed*delta*1.25f);
            movingUp = false;
        }
    }

    private void logic(){
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float heliWidth = heliSprite.getWidth();
        float heliHeight = heliSprite.getHeight();

        if(!isControlled()){
            float delta = Gdx.graphics.getDeltaTime();


            if(heliSprite.getX() >= worldWidth - heliWidth){
                movingRight = false;
                currentXSpeed = setRandomXSpeed();
            }else if(heliSprite.getX() <= 0){
                movingRight = true;
                currentXSpeed = setRandomXSpeed();
            }

            if(heliSprite.getY() >= worldHeight - heliHeight){
                movingUp = false;
                currentYSpeedDown = setRandomYSpeedDown();
            } else if (heliSprite.getY() <= 0) {
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

        heliSprite.setX(MathUtils.clamp(heliSprite.getX(), 0, worldWidth - heliWidth));
        heliSprite.setY(MathUtils.clamp(heliSprite.getY(), 0, worldHeight - heliHeight));


    }

    @Override
    public void dispose() {
        batch.dispose();
        heliTexture.dispose();
    }
}
