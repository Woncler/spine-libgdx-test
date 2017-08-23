package com.esotericsoftware.spine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class MyGdxGame extends ApplicationAdapter {
    private OrthographicCamera camera;
    private PolygonSpriteBatch batch;
    private SkeletonRenderer renderer;
    private SkeletonRendererDebug debugRenderer;

    private TextureAtlas atlas;
    private Skeleton skeleton;
    private AnimationState state;
    private boolean swapper = false;

	public void create () {
		camera = new OrthographicCamera();
		batch = new PolygonSpriteBatch();
		renderer = new SkeletonRenderer();
		renderer.setPremultipliedAlpha(true); // PMA results in correct blending without outlines.
		debugRenderer = new SkeletonRendererDebug();
		debugRenderer.setBoundingBoxes(false);
		debugRenderer.setRegionAttachments(false);

		changeCharacter();
	}

	public void render () {
		state.update(Gdx.graphics.getDeltaTime()); // Update the animation time.

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(1, 1, 1, 1);

		state.apply(skeleton); // Poses skeleton using current animations. This sets the bones' local SRT.
		skeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.

		// Configure the camera, SpriteBatch, and SkeletonRendererDebug.
		camera.update();
		batch.getProjectionMatrix().set(camera.combined);
		debugRenderer.getShapeRenderer().setProjectionMatrix(camera.combined);

		batch.begin();
		renderer.draw(batch, skeleton); // Draw the skeleton images.
		batch.end();

		debugRenderer.draw(skeleton); // Draw debug lines.
	}

	public void resize (int width, int height) {
		camera.setToOrtho(false); // Update camera with new size.
	}

	public void dispose () {
		atlas.dispose();
	}

	public void changeCharacter () {
	    pause();
	    if (swapper) {
            atlas = new TextureAtlas(Gdx.files.internal("raptor/raptor-pma.atlas"));
        } else {
            atlas = new TextureAtlas(Gdx.files.internal("spineboy/spineboy-pma.atlas"));
        }

        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        json.setScale(0.6f); // Load the skeleton at 60% the size it was in Spine.

        SkeletonData skeletonData;
        if (swapper) {
            skeletonData = json.readSkeletonData(Gdx.files.internal("raptor/raptor-pro.json"));
        } else {
            skeletonData = json.readSkeletonData(Gdx.files.internal("spineboy/spineboy-ess.json"));
        }

        skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone positions, slot attachments, etc).
        skeleton.setPosition(250, 20);

        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.

        state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
        state.setTimeScale(0.5f); // Slow all animations down to 50% speed.

        // Queue animations on track 0.
        state.setAnimation(0, "jump", true);

        swapper = !swapper;
        resume();
	}
}
