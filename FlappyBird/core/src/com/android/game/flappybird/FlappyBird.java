package com.android.game.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ColorInfluencer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.awt.Color;
import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture []passaros;
	private int contador = 0;
	private int movimento=0;
    private float larguraDispositivo;
    private float alturaDispositivo;
	private Texture fundo;
    private float variacao =0;
    private float velocidadeQueda=0;
    private float posicaoInicialVertical;
    private Texture canoTopo;
    private Texture canoBaixo;
    private Circle passaroCirculo;
    private Rectangle canoTopoRetangulo;
    private Rectangle canoBaixoRetangulo;
    private ShapeRenderer shape;
    private float posMovimentoCanoHorizontal;
    private float espacoCanos;
    private float deltaTime;
    private int alturaEntreCanos;
    private int estadoJogo = 0; //0-> jogo não iniciado | 1 -> jogo iniciado | 2-> game over
    private BitmapFont fonte;
    private int pontuacao=0;
    private boolean marcou=false;
    private Texture gameOver;
    private BitmapFont mensagem;
    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH =768;
    private final float VIRTUAL_HIGHT = 1024;


	@Override
	public void create () {

		//Gdx.app.log("Create","Jogo Inicializado");
		batch = new SpriteBatch();
		passaros = new Texture[3];
        passaroCirculo = new Circle();
        shape = new ShapeRenderer();
        canoTopoRetangulo = new Rectangle();
        canoBaixoRetangulo = new Rectangle();
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");
		fundo = new Texture("fundo.png");
        canoTopo = new Texture("cano_topo.png");
        canoBaixo = new Texture("cano_baixo.png");
        larguraDispositivo = VIRTUAL_WIDTH;
        alturaDispositivo = VIRTUAL_HIGHT;
        posicaoInicialVertical = alturaDispositivo/2;
        posMovimentoCanoHorizontal = larguraDispositivo;
        deltaTime = Gdx.graphics.getDeltaTime();
        espacoCanos = 400;
        fonte = new BitmapFont();
        fonte.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        fonte.getData().setScale(6);
        gameOver = new Texture("game_over.png");
        mensagem = new BitmapFont();
        mensagem.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        mensagem.getData().scale(3);

        //Configuração da câmera
        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH/2,VIRTUAL_HIGHT/2,0);
        viewport = new StretchViewport(VIRTUAL_WIDTH,VIRTUAL_HIGHT,camera);



	}

	@Override
	public void render () {

        camera.update();
        //limpar frames
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);

        variacao += Gdx.graphics.getDeltaTime() * 10;

        if (variacao > 2) {
            variacao = 0;
        }

        if(estadoJogo == 0)
        {
            if(Gdx.input.justTouched())
            {
                estadoJogo = 1;
            }
        }
        else { //jogo iniciado

            velocidadeQueda++;
            //Verificar queda
            if (posicaoInicialVertical > 0 || velocidadeQueda < 0) {
                posicaoInicialVertical = posicaoInicialVertical - velocidadeQueda;
            }

            if(estadoJogo == 1)
            {
                posMovimentoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 200;


                //verifica se houve um clique na tela
                if (Gdx.input.justTouched()) {
                    velocidadeQueda = -15;
                }



                //verifica se o cano saiu da tela
                if (posMovimentoCanoHorizontal < canoTopo.getWidth()) {
                    Random numeroRandomico = new Random();
                    posMovimentoCanoHorizontal = larguraDispositivo;
                    alturaEntreCanos = numeroRandomico.nextInt(400) - 200;
                    marcou =false;
                }
                //verifica pontuação
                if(posMovimentoCanoHorizontal < 120)
                {
                    if(!marcou)
                    {
                        pontuacao++;
                        marcou =true;

                    }

                }

            }
            else //tela game over
            {
                if(Gdx.input.justTouched())
                {
                    estadoJogo = 0;
                    pontuacao = 0;
                    velocidadeQueda = 0;
                    posicaoInicialVertical = alturaDispositivo/2;
                    posMovimentoCanoHorizontal = larguraDispositivo;
                }
            }


            }
            //configuração dados de projeção da camera
            batch.setProjectionMatrix(camera.combined);

            batch.begin();
            batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
            batch.draw(canoTopo, posMovimentoCanoHorizontal, alturaDispositivo / 2 + espacoCanos / 2 + alturaEntreCanos);
            batch.draw(canoBaixo, posMovimentoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoCanos / 2 + alturaEntreCanos);
            batch.draw(passaros[(int) variacao], 120, posicaoInicialVertical);
            fonte.draw(batch, String.valueOf(pontuacao),larguraDispositivo/2,alturaDispositivo - 50);
            if(estadoJogo ==2)
            {
                batch.draw(gameOver,larguraDispositivo/2-gameOver.getWidth()/2,alturaDispositivo/2-gameOver.getHeight()/2);
                mensagem.draw(batch,"Toque para reiniciar",larguraDispositivo/2-200,alturaDispositivo / 2-gameOver.getHeight()/2);
            }

            batch.end();

        passaroCirculo.set(120+passaros[0].getWidth()/2,posicaoInicialVertical + passaros[0].getHeight()/2,passaros[0].getWidth()/2);
        canoTopoRetangulo = new Rectangle(posMovimentoCanoHorizontal,alturaDispositivo / 2 + espacoCanos / 2 + alturaEntreCanos,canoTopo.getWidth(),canoTopo.getHeight());
        canoBaixoRetangulo = new Rectangle(posMovimentoCanoHorizontal,alturaDispositivo / 2 - canoBaixo.getHeight() - espacoCanos / 2 + alturaEntreCanos,canoBaixo.getWidth(),canoBaixo.getHeight());


        //Colisão
        if(Intersector.overlaps(passaroCirculo,canoBaixoRetangulo)||Intersector.overlaps(passaroCirculo,canoTopoRetangulo) || posicaoInicialVertical <= 0 || posicaoInicialVertical >= alturaDispositivo)
        {
            estadoJogo = 2;

        }

	}

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height);
    }
}
