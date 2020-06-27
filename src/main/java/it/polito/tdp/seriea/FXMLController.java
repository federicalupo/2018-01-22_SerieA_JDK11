package it.polito.tdp.seriea;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import it.polito.tdp.seriea.model.Model;
import it.polito.tdp.seriea.model.Team;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

public class FXMLController {
	
	private Model model;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ChoiceBox<Team> boxSquadra;

    @FXML
    private Button btnSelezionaSquadra;

    @FXML
    private Button btnTrovaAnnataOro;

    @FXML
    private Button btnTrovaCamminoVirtuoso;

    @FXML
    private TextArea txtResult;

    @FXML
    void doSelezionaSquadra(ActionEvent event) {
    	this.txtResult.clear();
    	
    	Team team = this.boxSquadra.getValue();
    	
    	this.txtResult.appendText("Punti in classifica di "+team+":\n");
    	
    	Map<Integer, Integer> punteggi = model.punteggi(team);
   
    	
    	for(Integer i : punteggi.keySet()) {
    		this.txtResult.appendText(model.getStagioniIdMap().get(i)+" "+punteggi.get(i)+"\n");
    	}
    }

    @FXML
    void doTrovaAnnataOro(ActionEvent event) {
    	this.txtResult.clear();
    	
    	Team team = this.boxSquadra.getValue();
    	
    	model.creaGrafo(team);
    	
    	this.txtResult.appendText("Grafo creato!\n#vertici: "+model.nVertici()+"\n#archi: "+model.nArchi()+"\n");
    	this.txtResult.appendText("\nAnnata d'oro: \n"+model.annataOro());

    }

    @FXML
    void doTrovaCamminoVirtuoso(ActionEvent event) {
    	this.txtResult.clear();
    	this.txtResult.appendText("Cammino virtuoso: \n");
    	

    	
    	for(Integer i : this.model.camminoVirtuoso().keySet()) {
    		this.txtResult.appendText(this.model.getStagioniIdMap().get(i)+" "+model.camminoVirtuoso().get(i)+"\n");
    	}

    }

    @FXML
    void initialize() {
        assert boxSquadra != null : "fx:id=\"boxSquadra\" was not injected: check your FXML file 'SerieA.fxml'.";
        assert btnSelezionaSquadra != null : "fx:id=\"btnSelezionaSquadra\" was not injected: check your FXML file 'SerieA.fxml'.";
        assert btnTrovaAnnataOro != null : "fx:id=\"btnTrovaAnnataOro\" was not injected: check your FXML file 'SerieA.fxml'.";
        assert btnTrovaCamminoVirtuoso != null : "fx:id=\"btnTrovaCamminoVirtuoso\" was not injected: check your FXML file 'SerieA.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'SerieA.fxml'.";

    }

	public void setModel(Model model) {
		this.model = model;
		this.boxSquadra.getItems().addAll(model.tendina());
		this.boxSquadra.setValue(model.tendina().get(0));
	}
}
