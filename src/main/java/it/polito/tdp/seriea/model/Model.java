package it.polito.tdp.seriea.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.seriea.db.SerieADAO;

public class Model {

	private SerieADAO dao;
	private Graph<Integer, DefaultWeightedEdge> grafo;
	private Map<Integer, Integer> punteggi; //contiene tutte le stagioni
	private List<Integer> stagioni; //solo le stagioni in cui gioca => vertici
	private Map<Integer, Integer> migliore;
	private Map<Integer, Season> stagioniIdMap;

	
	
	public Model() {
		dao = new SerieADAO();
	}
	
	public List<Team> tendina(){
		return dao.listTeams();
	}
	
	public Map<Integer, Integer> punteggi(Team team) {
		punteggi = new LinkedHashMap<>(); //altrimenti non mantiene ordine di inserimento
		this.stagioniIdMap = new LinkedHashMap<>();
		
		for(Season s : dao.listAllSeasons()) {
			
			punteggi.put(s.getSeason(), 0);
			stagioniIdMap.put(s.getSeason(), s);
		}
		
		
		dao.punteggio(team, punteggi);
		return punteggi;
	}
	
	public void creaGrafo(Team team) {
		this.grafo = new SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.stagioni = dao.stagioni(team);
		
		Graphs.addAllVertices(this.grafo, stagioni);
		
		
		for(Integer i : grafo.vertexSet()) {
			for(Integer i2: grafo.vertexSet()) {
				if(i.compareTo(i2)<0) {
					Integer punti1 = punteggi.get(i);
					Integer punti2 = punteggi.get(i2);
					
					if(punti1.compareTo(punti2)>0) {
						Graphs.addEdge(this.grafo, i2, i, (punti1-punti2));
						
					}else {
						Graphs.addEdge(this.grafo, i, i2, (punti2-punti1));
					}
				}
				
			}
		}
	}
	
	public String annataOro() {
		Integer max = Integer.MIN_VALUE;
		Integer anno = null;
		
		
		for(Integer i : grafo.vertexSet()) {
			Integer entranti=0;
			Integer uscenti = 0;
			
			//archi entranti 
			for(DefaultWeightedEdge d: this.grafo.incomingEdgesOf(i)) {
				entranti+=(int) this.grafo.getEdgeWeight(d);
			}
			
			//archi uscenti
			
			for(DefaultWeightedEdge d: this.grafo.outgoingEdgesOf(i)) {
				uscenti+=(int) this.grafo.getEdgeWeight(d);
			}
			
			Integer differenza = entranti-uscenti;
			
			if(differenza > max) {
				max=differenza;
				anno = i;
			}

		}
		
		return "Anno: "+this.stagioniIdMap.get(anno)+"  Differenza: "+max;
	}

	public Integer nVertici() {
		return this.grafo.vertexSet().size();
	}
	public Integer nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	/**RICORSIVA:
	 * 
	 * cammino piu lungo di miglioramenti consecutivi => ad ogni stagione miglioro
	 * 
	 * parziale = stagioni consecutive fin quando c'è miglioramento
	 * 
	 * parto dalla prima stagione
	 * mi faccio dare i successivi (stagioni migliori)
	 * vedo se tra queste c'è la stagione successiva
	 * se c'è vuol dire che nella stagione succ c'è miglioramento quindi aggiungo
	 * 
	 * altrimenti non c'è miglioramento, creo nuova struttura e parto dalla stagione in cui mi sono fermata (livello)
	 * 
	 * terminazione => 
	 * se livello == stagione.size
	 * soluzione finale => se la size > sizeMax => migliore
	 * 
	 * livello = posizione della stagione nella lista
	 */
	public Map<Integer, Integer> camminoVirtuoso() {
		this.migliore = new LinkedHashMap<>(); //provo anche hashmap
		Map<Integer, Integer> parziale = new LinkedHashMap<>();
		
		//se parto dalla prima => o migliora alla seconda oppure non trovo miglioramenti 
		//magari c'è un miglioramento alla 2° stagione
		

		ricorsiva(parziale, 0, null);
		return this.migliore;
		
		
	}
	
	private void ricorsiva(Map<Integer, Integer> parziale, Integer livello, Integer ultimaStagione){
	
			if(parziale.size() > migliore.size()) {
				this.migliore = new LinkedHashMap<>(parziale);
			}
			if(livello.compareTo(stagioni.size())==0) { //altrimenti continua a cercare miglioramenti
				return;
			}
		
	
			if(parziale.size()==0) {
				parziale.put(this.stagioni.get(livello), punteggi.get(this.stagioni.get(livello)));
				ricorsiva(parziale, livello+1, this.stagioni.get(livello));
			}else {
		
				
				List<Integer> uscenti = Graphs.successorListOf(this.grafo, ultimaStagione); //punteggio migliore
				if(!uscenti.contains(this.stagioni.get(livello))) { //decrementa, ricomincio da capo a partire dalla stagione in posizione livello
					
					parziale = new LinkedHashMap<>();
					ricorsiva(parziale, livello ,null);
					
					
				}else {
					parziale.put(this.stagioni.get(livello), this.punteggi.get(this.stagioni.get(livello)));
					ricorsiva(parziale, livello+1, this.stagioni.get(livello));	
				}
			
			}
		
		
	}

	public Map<Integer, Season> getStagioniIdMap() {
		return stagioniIdMap;
	}
	
}
