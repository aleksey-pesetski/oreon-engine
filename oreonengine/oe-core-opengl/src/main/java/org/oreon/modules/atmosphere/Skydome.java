package org.oreon.modules.atmosphere;

import org.oreon.core.gl.buffers.GLMeshVBO;
import org.oreon.core.gl.config.CullFaceDisable;
import org.oreon.core.model.Mesh;
import org.oreon.core.renderer.RenderInfo;
import org.oreon.core.renderer.Renderer;
import org.oreon.core.scene.GameObject;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.texture.ProceduralTexturing;
import org.oreon.core.utils.modelLoader.obj.OBJLoader;

public class Skydome extends GameObject{
	
	public Skydome()
	{
		Mesh mesh = new OBJLoader().load("models/obj/dome", "dome.obj", null)[0].getMesh();
		ProceduralTexturing.dome(mesh);
		GLMeshVBO meshBuffer = new GLMeshVBO();
		meshBuffer.addData(mesh);
		Renderer renderer = new Renderer(meshBuffer);
		renderer.setRenderInfo(new RenderInfo(new CullFaceDisable(),AtmosphereShader.getInstance()));
		addComponent("Renderer", renderer);
	}
	
	public void update()
	{	
		getWorldTransform().setRotation(getWorldTransform().getLocalRotation().add(getParent().getWorldTransform().getRotation()));
		getWorldTransform().setTranslation(getWorldTransform().getLocalTranslation().add(getParent().getWorldTransform().getTranslation()));
		getWorldTransform().setScaling(getWorldTransform().getLocalScaling().mul(getParent().getWorldTransform().getScaling()));
	}
	
	public void render() {
		if (CoreSystem.getInstance().getRenderingEngine().isWaterRefraction() && !CoreSystem.getInstance().getRenderingEngine().isCameraUnderWater()){
			return;
		}
		if (CoreSystem.getInstance().getRenderingEngine().isGrid()){
			return;
		}
		else {
			super.render();
		}
	}
}
