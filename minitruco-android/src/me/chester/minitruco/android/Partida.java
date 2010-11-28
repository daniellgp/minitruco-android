package me.chester.minitruco.android;

import me.chester.minitruco.R;
import me.chester.minitruco.core.Carta;
import me.chester.minitruco.core.Interessado;
import me.chester.minitruco.core.Jogador;
import me.chester.minitruco.core.Jogo;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;

/**
 * Exibe o andamento de um jogo.
 * 
 * @author chester
 * 
 */
public class Partida extends Activity implements Interessado {

	protected static final int WHAT_TRUCO = 3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.partida);
		mesa = (MesaView) findViewById(R.id.MesaView01);
		// Inicializa componentes das classes visuais que dependem de métodos
		// disponíveis exclusivamente na Activity
		if (MesaView.iconesRodadas == null) {
			MesaView.iconesRodadas = new Bitmap[4];
			MesaView.iconesRodadas[0] = ((BitmapDrawable) getResources()
					.getDrawable(R.drawable.placarrodada0)).getBitmap();
			MesaView.iconesRodadas[1] = ((BitmapDrawable) getResources()
					.getDrawable(R.drawable.placarrodada1)).getBitmap();
			MesaView.iconesRodadas[2] = ((BitmapDrawable) getResources()
					.getDrawable(R.drawable.placarrodada2)).getBitmap();
			MesaView.iconesRodadas[3] = ((BitmapDrawable) getResources()
					.getDrawable(R.drawable.placarrodada3)).getBitmap();
		}
		if (CartaVisual.resources == null) {
			CartaVisual.resources = getResources();
		}

		// Prepara diálogo da mão de 11
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				jogo.decideMao11(jogo.getJogadorHumano(),
						which == DialogInterface.BUTTON_POSITIVE);
			}
		};

		// Assumindo que o menu principal já adicionou os jogadores ao jogo,
		// inscreve a Mesa como interessado e inicia o jogo em sua própria
		// thread.
		jogo = MenuPrincipal.jogo;
		if (jogo != null) {
			jogo.adiciona(this);
			MesaView.jogo = jogo;
		} else {
			Log.w("Activity.onCreate",
					"Partida iniciada sem jogo (ok para testes)");
		}

	}

	public MesaView mesa;

	Jogo jogo;

	// /**
	// * Recebe mensagens (da view, principalmente) e executa usando a thread de
	// * UI (senão o Android não deixa).
	// */
	// public final Handler handler = new Handler() {
	// public void handleMessage(Message msg) {
	// // MesaView mesa = (MesaView) findViewById(R.id.MesaView01);
	// if (msg.what == WHAT_TRUCO) {
	//
	// }
	// }
	// };

	public void print(String s) {
		Log.i("Partida.print", s);
	}

	public void aceitouAumentoAposta(Jogador j, int valor) {
		MesaView.aguardaFimAnimacoes();
		Balao.diz("desce", j.getPosicao(), 1500);
	}

	public void cartaJogada(Jogador j, Carta c) {
		MesaView.aguardaFimAnimacoes();
		mesa.descarta(c, j.getPosicao());
		// TODO Auto-generated method stub
		print("Jogador " + j.getPosicao() + " jogou " + c);
	}

	public void decidiuMao11(Jogador j, boolean aceita) {
		if (j.getPosicao() != 1)
			decidiuMao11 = aceita;
		MesaView.aguardaFimAnimacoes();
		mesa.mostrarPerguntaMao11 = false;
		Balao.diz(aceita ? "Vamos jogar" : "Não quero", j.getPosicao(), 1500);
	}

	public void entrouNoJogo(Interessado i, Jogo j) {
		// TODO Auto-generated method stub

	}

	public void informaMao11(Carta[] cartasParceiro) {
		// TODO mostrar cartas do adversário
		// MesaView.aguardaFimAnimacoes();
		if (jogo.getJogadorHumano() != null && !decidiuMao11) {
			mesa.mostrarPerguntaMao11 = true;
		}

	}

	private boolean decidiuMao11 = false;

	public void inicioMao() {
		decidiuMao11 = false;
		MesaView.aguardaFimAnimacoes();
		for (int i = 0; i <= 2; i++) {
			mesa.resultadoRodada[i] = 0;
		}
		mesa.distribuiMao();
	}

	public void inicioPartida() {
		// TODO Auto-generated method stub

	}

	public void jogoAbortado(int posicao) {
		// TODO Auto-generated method stub

	}

	public void jogoFechado(int numEquipeVencedora) {
		// TODO Auto-generated method stub
		print("Jogo fechado. Equipe vencedora:" + numEquipeVencedora);
	}

	public void maoFechada(int[] pontosEquipe) {
		MesaView.aguardaFimAnimacoes();
		mesa.atualizaPontosEquipe(pontosEquipe);
		MesaView.aguardaFimAnimacoes();		
		mesa.recolheMao();

	}

	public void pediuAumentoAposta(Jogador j, int valor) {
		MesaView.aguardaFimAnimacoes();
		Balao.diz("Truco!", j.getPosicao(), 1500 + 200 * (valor / 3));
		if (j.getEquipe() == 2 && jogo.getJogadorHumano() != null) {
			mesa.mostrarPerguntaAumento = true;
		}
	}

	public void recusouAumentoAposta(Jogador j) {
		MesaView.aguardaFimAnimacoes();
		Balao.diz("não quero", j.getPosicao(), 1300);
	}

	public void rodadaFechada(int numRodada, int resultado,
			Jogador jogadorQueTorna) {
		mesa.mostrarPerguntaMao11 = false;
		mesa.mostrarPerguntaAumento = false;
		MesaView.aguardaFimAnimacoes();
		mesa.atualizaResultadoRodada(numRodada, resultado, jogadorQueTorna);
	}

	public void vez(Jogador j, boolean podeFechada) {
		mesa.mostrarPerguntaMao11 = false;
		mesa.mostrarPerguntaAumento = false;
		MesaView.setVezHumano(j instanceof JogadorHumano);
	}

	// // Mensagens para a thread da UI
	// protected static final int WHAT_INVALIDATE = -1;

}
