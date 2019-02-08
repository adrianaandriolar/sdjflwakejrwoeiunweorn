package online.dwResources;

import java.io.File;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import online.configuration.TopTrumpsJSONConfiguration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import commandline.Card;
import commandline.HumanPlayer;
import commandline.ImportDeckInformation;
import commandline.AIPlayer;
import commandline.Player;
import commandline.TopTrumpsCLIApplication;

@Path("/toptrumps") // Resources specified here should be hosted at http://localhost:7777/toptrumps
@Produces(MediaType.APPLICATION_JSON) // This resource returns JSON content
@Consumes(MediaType.APPLICATION_JSON) // This resource can take JSON content as input
/**
 * This is a Dropwizard Resource that specifies what to provide when a user
 * requests a particular URL. In this case, the URLs are associated to the
 * different REST API methods that you will need to expose the game commands
 * to the Web page.
 * 
 * Below are provided some sample methods that illustrate how to create
 * REST API methods in Dropwizard. You will need to replace these with
 * methods that allow a TopTrumps game to be controled from a Web page.
 */

public class TopTrumpsRESTAPI {

	/** A Jackson Object writer. It allows us to turn Java objects
	 * into JSON strings easily. */
	ObjectWriter oWriter = new ObjectMapper().writerWithDefaultPrettyPrinter();
	
	private File deckFile;
	private int numPlayers;
	private ImportDeckInformation gameDeck;
	private Player activePlayer;
	private int catIndex;
	private int numRounds;
	private int numDraws;
	private HumanPlayer humanPlayer;
	private Player winner;
	private Player gameWinner;
	private int[] playerWinCounts = new int[5];
	private static ArrayList <Player> players;
	private ArrayList<Card> winnerPile;
	/**
	 * Contructor method for the REST API. This is called first. It provides
	 * a TopTrumpsJSONConfiguration from which you can get the location of
	 * the deck file and the number of AI players.
	 * @param conf
	 */
	public TopTrumpsRESTAPI(TopTrumpsJSONConfiguration conf) {
	
		// ----------------------------------------------------
		// Add relevant initalization here
		// ----------------------------------------------------
	deckFile = conf.getDeckFile();
	numPlayers = conf.getNumAIPlayers() + 1;
	
	}
	
	// ----------------------------------------------------
	// Add relevant API methods here
	// ----------------------------------------------------
	
	@GET
	@Path("/setPlayers")
	@Consumes(MediaType.APPLICATION_JSON)
	public void setPlayers(@QueryParam("Number") int Number) throws IOException {
		numPlayers = Number + 1;
	//	System.err.println("the number of players: " + numPlayers); testing
		startGame1();
	}
	@GET
	@Path("/activePlayer")
	public String activePlayer() throws IOException {
	//	System.err.println("Active player is " + activePlayer.getName()); testing

		if (activePlayer != humanPlayer) {
			computerSelect();
		}

		return activePlayer.getName();
	}
	/*
	 * method that creates game based on number of players and declares a
	 * winnerPile this method also will randomise the order of players for who
	 * goes first. Round number is set to 1.
	 */
	public void playGame() {
		gameDeck = new ImportDeckInformation(deckFile);
		Deck.shuffle(gameDeck.getDeck());

		Deck[] deck = gameDeck.advancedSplit(this.numPlayers);
		this.humanPlayer = new HumanPlayer("Human Player", deck[0]);
		players = new ArrayList<Player>();
		players.add(humanPlayer);

		winnerPile = new ArrayList<Card>();
		for (int i = 1; i < deck.length; i++) {
			Player p = new Player("Computer " + i, deck[i]);
			players.add(p);

		}
		randomiseOrder();
		numRounds = 1;
	}
	public void randomiseOrder() {
		Collections.shuffle(players);
		activePlayer = players.get(0);
	}

	public void removePlayer(int i) {
		players.remove(i);
	}

	// returns an index depending on the button pressed
	// index is used to set the chosen category
	@GET
	@Path("/computerSelect")
	public String computerSelect() {

		catIndex = activePlayer.chooseCategory();
		String catString = activePlayer.getName().getSelectedCategory(catIndex);
		return catString;

	}
	@GET
	@Path("/selectCategory")
	public String selectCategory(@QueryParam("Number") int Number) throws IOException {
		catIndex = Number - 1;
		String catString = activePlayer.getName().getCatChoice(catIndex);
		return catString;

	}

	// a count for player decks remaining in game
	public int checkDecks() {
		int count = 0;
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i) != null && players.get(i).getDeckSize() > 0)
				count++;
		}
		return count;
	}
	@GET
	@Path("/processRound")
	public String processRound() throws JsonProcessingException {

		for (int i = 0; i < players.size(); i++) {
			// checks to see if any players have run out of cards
			if (players.get(i) != null && players.get(i).getDeckSize() < 1) {
				players.set(i, null);
			}
		}

		if (checkDecks() > 1) {

			numRounds++;

			for (Player p : players) {
				if (p != null) {
					p.getHeldCard().setSelectedValue(catIndex);
					// assigns the above value to each player
					p.setChosenCat(p.getHeldCard().getSelectedValue());

					p.getDeck().remove(0);
				}
			}
			compareCards();
			
			//if draw return top two players, else give top player winnerPile and clear it
			if (players.get(0).compareTo(players.get(1)) == 0) {
				String draw = "Draw between " + players.get(0).getName() + " & " + players.get(1).getName();
			//	System.err.println("DRAW~~~~~~~~~~~~~");
				return draw;
			} else {

				winner = players.get(0);

				// winner gets winner pile - cards are added to pile in
				activePlayer = winner;
				winner.addToDeck(winnerPile);

				incrementPlayerWins();
				winnerPile.clear();

				return winner.getName();
			}
		} else {
		//	System.err.println(winner.getName() + " HAS WON THE GAME"); testing
			return "EndGame";
		}
	}
	
	/**
	 * Method is used to sort the player array. The winning player is sorted towards the
	 * start of the array. It accounts for null values in players and moves them towards
	 * the back of the array.
	 * It is used to determine the winner or if a draw has occurred
	 * */
	public void compareCards() {
		Collections.sort(players, new Comparator<Player>() {
			public int compare(Player p1, Player p2) {
				if (p1 == null) {
					return 1;
				} else if (p2 == null) {
					return -1;
				} else {
					return (p1.compareTo(p2) * -1);
				}
			}
		});	
	}

	
	
	
	/**
	 * Method to populate the saved statistics of the game
	 * @return xAsJsonString - String representation of array containing pertinent information
	 * game statistics
	 * */
	@GET
	@Path("/statsTable")
	public String viewStatistics() throws IOException {
		Database db = new Database();
		int[] x = db.getGameStatisticsOnline();
		db.closeConnection();
		db = null;

		String xAsJsonString = oWriter.writeValueAsString(x);
		return xAsJsonString;
	}

	/**
	 * Saves game statistics to database at the end of the game.
	 */
	public void saveGameStats() {
		Database db = new Database();
		if (numPlayers == 2)
			db.gameStats(gameWinner.getName(), numRounds, numDraws, playerWinCounts[0], playerWinCounts[1]);
		else if (numPlayers == 3)
			db.gameStats(gameWinner.getName(), numRounds, numDraws, playerWinCounts[0], playerWinCounts[1],
					playerWinCounts[2]);
		else if (numPlayers == 4)
			db.gameStats(gameWinner.getName(), numRounds, numDraws, playerWinCounts[0], playerWinCounts[1],
					playerWinCounts[2], playerWinCounts[3]);
		else if (numPlayers == 4)
			db.gameStats(gameWinner.getName(), numRounds, numDraws, playerWinCounts[0], playerWinCounts[1],
					playerWinCounts[2], playerWinCounts[3]);
		else if (numPlayers == 5)
			db.gameStats(gameWinner.getName(), numRounds, numDraws, playerWinCounts[0], playerWinCounts[1],
					playerWinCounts[2], playerWinCounts[3], playerWinCounts[4]);
		db.closeConnection();
	}
}



