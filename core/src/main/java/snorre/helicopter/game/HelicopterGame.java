package snorre.helicopter.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.awt.Rectangle;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class HelicopterGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture heliTexture;
    private Sprite heliSprite;
    private Rectangle heliRect;
    private FitViewport viewport;

    @Override
    public void create() {
        batch = new SpriteBatch();
        heliTexture = new Texture("heli-sprite.png");
        heliSprite = new Sprite(heliTexture);
        heliSprite.setSize(1,1);
        heliRect = new Rectangle();
        viewport = new FitViewport(8,5);
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

    private boolean movingRight = true;

    private void logic(){
        float speed = .75f;
        float delta = Gdx.graphics.getDeltaTime();
        float worldWidth = viewport.getWorldWidth();
        float heliWidth = heliSprite.getWidth();

        
        if(heliSprite.getX() > worldWidth - heliWidth){
            movingRight = false;
        }else if(heliSprite.getX() < 0){
            movingRight = true;
        }

        if(movingRight){
            heliSprite.translateX(speed*delta);
        }else {
            heliSprite.translateX(-speed*delta);
        }

    }

    @Override
    public void dispose() {
        batch.dispose();
        heliTexture.dispose();
    }
}
