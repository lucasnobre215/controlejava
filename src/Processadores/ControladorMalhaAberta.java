/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Processadores;

import Graficos.FuncaoTimeSeries;
import Graficos.TimeSeriesChart;
import br.ufrn.dca.controle.QuanserClient;
import br.ufrn.dca.controle.QuanserClientException;
import funcoes.Onda;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.ConfiguracaoProjeto;
import util.TravaSeguranca;
import static java.lang.Thread.sleep;
import static java.lang.Thread.sleep;
import static java.lang.Thread.sleep;

/**
 *
 * @author lucasnobre
 */
public class ControladorMalhaAberta extends Controlador {

    ConexaoQuanser conexao;
    double tensaoSaida;
    double tensaoSegura;
    double tensaoNivelSeguro;

    public ControladorMalhaAberta(ConfiguracaoProjeto cfg, TimeSeriesChart graficoFuncao, TimeSeriesChart graficoNivel) {
        super(cfg, graficoFuncao, graficoNivel);
       conexao = new ConexaoQuanser(cfg);
    }

    @Override
    public void run() {
        float tempo = 0;
        cfg.setIsRunning(true);
        try {
            // A classe chamada onda tem uma interface, tem que implementar o calcular, tambem recebe a classe de configuracao
            //Essa classe implementa uma thread
            while (cfg.isRunning()) {
                tempo += 0.1;
                tensaoSaida = cfg.getOnda().calcular(tempo);
                conexao.readValue(0);
                tensaoSegura = TravaSeguranca.limitarTensaoMaxima(tensaoSaida);
                tensaoNivelSeguro = TravaSeguranca.limitarTensaoPorNivelTanque(cfg.getValorSensor(), tensaoSegura);
                graficoFuncao.atualizarGrafico(tensaoNivelSeguro);
                graficoNivel.atualizarGrafico(cfg.getValorSensor());
                conexao.writeValue(0, tensaoNivelSeguro);
                sleep(100);
            }
            conexao.writeValue(0, 0d);
        } catch (InterruptedException ex) {
            Logger.getLogger(ControladorMalhaAberta.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
