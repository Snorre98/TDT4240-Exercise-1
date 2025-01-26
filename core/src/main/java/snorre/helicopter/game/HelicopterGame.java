package snorre.helicopter.game;

import static java.lang.String.*;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.Random;

public class HelicopterGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture heliTexture;
    private Sprite heliSprite;
    private FitViewport viewport;
    private Vector2 touchPos;
    private Vector2 heliVelocity;
    private Vector2 attackHeliVelocity;

    private boolean movingRight = true;
    private boolean movingUp = true;
    private boolean attackHeliMovingRight = true;
    private boolean attackHeliMovingUp = true;

    private BitmapFont font;
    private GlyphLayout glyXPos;
    private GlyphLayout glyYPos;

    private Animation<TextureRegion> heliAnimation;
    private float stateTime;

    private Texture attackHeliTexture;
    private Sprite attackHeliSprite;
    private Rectangle heliBounds;
    private Rectangle attackHeliBounds;

    @Override
    public void create() {
        batch = new SpriteBatch();
        heliTexture = new Texture("heli1.png");
        heliSprite = new Sprite(heliTexture);
        heliSprite.setSize(3, 1);
        heliSprite.setPosition(2, 2);

        attackHeliTexture = new Texture("attackhelicopter.PNG");
        attackHeliSprite = new Sprite(attackHeliTexture);
        attackHeliSprite.setSize(3, 1);
        attackHeliSprite.setPosition(10, 10);

        // Initialize velocities
        heliVelocity = new Vector2(setRandomXSpeed(), setRandomYSpeedUp());
        attackHeliVelocity = new Vector2(setRandomXSpeed(), setRandomYSpeedUp());

        // Initialize collision bounds
        heliBounds = new Rectangle();
        attackHeliBounds = new Rectangle();

        viewport = new FitViewport(16, 16);
        touchPos = new Vector2();

        // Animation setup
        Texture frame1 = new Texture("heli1.png");
        Texture frame2 = new Texture("heli2.png");
        Texture frame3 = new Texture("heli3.png");
        TextureRegion[] frames = new TextureRegion[3];
        frames[0] = new TextureRegion(frame1);
        frames[1] = new TextureRegion(frame2);
        frames[2] = new TextureRegion(frame3);

        heliAnimation = new Animation<>(0.1f, frames);
        stateTime = 0f;

        glyXPos = new GlyphLayout();
        glyYPos = new GlyphLayout();

        font = new BitmapFont(Gdx.files.internal("arial.fnt"));
        font.getData().setScale(0.04f);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        draw();
        input();
        logic();
        checkCollisions();
    }

    private void checkCollisions() {
        // Update collision bounds
        heliBounds.set(heliSprite.getX(), heliSprite.getY(),
            heliSprite.getWidth(), heliSprite.getHeight());
        attackHeliBounds.set(attackHeliSprite.getX(), attackHeliSprite.getY(),
            attackHeliSprite.getWidth(), attackHeliSprite.getHeight());

        // Check for collision
        if (heliBounds.overlaps(attackHeliBounds)) {
            // Bounce effect with damping
            float antiBounce = 0.7f; // Reduces velocity after collision
            float tempX = heliVelocity.x;
            float tempY = heliVelocity.y;

            heliVelocity.x = -attackHeliVelocity.x * antiBounce;
            heliVelocity.y = -attackHeliVelocity.y * antiBounce;

            attackHeliVelocity.x = -tempX * antiBounce;
            attackHeliVelocity.y = -tempY * antiBounce;

            // Slightly separate the sprites to prevent sticking
            float pushDistance = 0.1f;
            if (heliSprite.getX() < attackHeliSprite.getX()) {
                heliSprite.translateX(-pushDistance);
                attackHeliSprite.translateX(pushDistance);
            } else {
                heliSprite.translateX(pushDistance);
                attackHeliSprite.translateX(-pushDistance);
            }
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();

        // Draw main helicopter
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = heliAnimation.getKeyFrame(stateTime, true);
        currentFrame.flip(movingRight, false);
        batch.draw(currentFrame, heliSprite.getX(), heliSprite.getY(),
            heliSprite.getWidth(), heliSprite.getHeight());
        currentFrame.flip(movingRight, false);

        // Draw attack helicopter
        attackHeliSprite.setFlip(!attackHeliMovingRight, false);
        attackHeliSprite.draw(batch);

        // Draw position info
        String xPos = String.format("X: %2d", (int)heliSprite.getX());
        String yPos = String.format("Y: %2d", (int)heliSprite.getY());
        glyXPos.setText(font, xPos);
        glyYPos.setText(font, yPos);
        font.draw(batch, glyXPos, .1f, viewport.getWorldHeight() - glyXPos.height);
        font.draw(batch, glyYPos, .1f, viewport.getWorldHeight() - glyXPos.height - glyYPos.height);

        batch.end();
    }

    private void logic() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float delta = Gdx.graphics.getDeltaTime();
        float friction = 0.995f; // Slight friction to counter endless bouncing

        if (!isControlled()) {
            // Apply friction to velocities
            heliVelocity.x *= friction;
            heliVelocity.y *= friction;

            // Reset velocity if it gets too low
            if (Math.abs(heliVelocity.x) < 0.5f) {
                heliVelocity.x = setRandomXSpeed() * (heliVelocity.x > 0 ? 1 : -1);
            }
            if (Math.abs(heliVelocity.y) < 0.2f) {
                heliVelocity.y = setRandomYSpeedUp() * (heliVelocity.y > 0 ? 1 : -1);
            }

            // Update main helicopter position
            heliSprite.translate(heliVelocity.x * delta, heliVelocity.y * delta);

            // Bounce off walls for main helicopter with damping
            float wallDamping = 0.8f;
            if (heliSprite.getX() <= 0 || heliSprite.getX() >= worldWidth - heliSprite.getWidth()) {
                heliVelocity.x = -heliVelocity.x * wallDamping;
                movingRight = heliVelocity.x > 0;
            }
            if (heliSprite.getY() <= 0 || heliSprite.getY() >= worldHeight - heliSprite.getHeight()) {
                heliVelocity.y = -heliVelocity.y * wallDamping;
                movingUp = heliVelocity.y > 0;
            }
        }

        // Apply friction to attack helicopter
        attackHeliVelocity.x *= friction;
        attackHeliVelocity.y *= friction;

        // Reset attack helicopter velocity if too low
        if (Math.abs(attackHeliVelocity.x) < 0.5f) {
            attackHeliVelocity.x = setRandomXSpeed() * (attackHeliVelocity.x > 0 ? 1 : -1);
        }
        if (Math.abs(attackHeliVelocity.y) < 0.2f) {
            attackHeliVelocity.y = setRandomYSpeedUp() * (attackHeliVelocity.y > 0 ? 1 : -1);
        }

        // Update attack helicopter position
        attackHeliSprite.translate(attackHeliVelocity.x * delta, attackHeliVelocity.y * delta);

        // Bounce off walls for attack helicopter with damping
        float wallDamping = 0.8f;
        if (attackHeliSprite.getX() <= 0 || attackHeliSprite.getX() >= worldWidth - attackHeliSprite.getWidth()) {
            attackHeliVelocity.x = -attackHeliVelocity.x * wallDamping;
            attackHeliMovingRight = attackHeliVelocity.x > 0;
        }
        if (attackHeliSprite.getY() <= 0 || attackHeliSprite.getY() >= worldHeight - attackHeliSprite.getHeight()) {
            attackHeliVelocity.y = -attackHeliVelocity.y * wallDamping;
            attackHeliMovingUp = attackHeliVelocity.y > 0;
        }

        // Clamp positions to screen bounds
        heliSprite.setPosition(
            MathUtils.clamp(heliSprite.getX(), 0, worldWidth - heliSprite.getWidth()),
            MathUtils.clamp(heliSprite.getY(), 0, worldHeight - heliSprite.getHeight())
        );

        attackHeliSprite.setPosition(
            MathUtils.clamp(attackHeliSprite.getX(), 0, worldWidth - attackHeliSprite.getWidth()),
            MathUtils.clamp(attackHeliSprite.getY(), 0, worldHeight - attackHeliSprite.getHeight())
        );
    }

    private float setRandomXSpeed() {
        float min = 0.8f;
        float max = 1.2f;
        Random r = new Random();
        return min + r.nextFloat() * (max - min);
    }

    private float setRandomYSpeedUp() {
        float min = 0.2f;
        float max = 0.3f;
        Random r = new Random();
        return min + r.nextFloat() * (max - min);
    }

    private float setRandomYSpeedDown() {
        float min = 0.8f;
        float max = 1.0f;
        Random r = new Random();
        return min + r.nextFloat() * (max - min);
    }

    private boolean isControlled() {
        return Gdx.input.isKeyPressed(Input.Keys.RIGHT)
            || Gdx.input.isKeyPressed(Input.Keys.LEFT)
            || Gdx.input.isKeyPressed(Input.Keys.UP)
            || Gdx.input.isKeyPressed(Input.Keys.DOWN)
            || Gdx.input.isTouched();
    }

    private void input() {
        float speed = 3.5f;
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            heliSprite.translateX(speed * delta);
            movingRight = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            heliSprite.translateX(-speed * delta);
            movingRight = false;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            heliSprite.translateY(speed * delta * 0.75f);
            movingUp = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            heliSprite.translateY(-speed * delta * 1.25f);
            movingUp = false;
        }

        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);
            heliSprite.setCenter(touchPos.x, touchPos.y);
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        for (TextureRegion frame : heliAnimation.getKeyFrames()) {
            frame.getTexture().dispose();
        }
        attackHeliTexture.dispose();
        font.dispose();
    }
}
