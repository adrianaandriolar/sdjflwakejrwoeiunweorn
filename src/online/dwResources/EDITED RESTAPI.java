package online.dwResources;

import java.io.File;
import java.io.IOException;


import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Collections;

import online.configuration.TopTrumpsJSONConfiguration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.sun.javafx.collections.MappingChange.Map;

import commandline.Card;
import commandline.HumanPlayer;
import commandline.ImportDeckInformation;
import commandline.AIPlayer;
import commandline.Player;
import commandline.TopTrumpsCLIApplication;


@Path("/toptrumps") // Resources specified here should be hosted at http://localhost:7777/toptrumps
@Produces(MediaType.APPLICATION_JSON) // This resource returns JSON content//
@Consumes(MediaType.APPLICATION_JSON) // This resource can take JSON content as input
/*
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

	private static final int ArrayList = 0;

	private static final int Card = 0;

	//A Jackson Object writer. It allows us to turn Java objects
	//  into JSON strings easily. 
	 ObjectWriter oWriter = new ObjectMapper().writerWithDefaultPrettyPrinter();
	
	private String deckFile;
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
	private String[] aNames;
	private String name;

	

	private ArrayList<Card> winnerPile;

	private Object c;
	/**
	 * Contructor method for the REST API. This is called first. It provides
	 * a TopTrumpsJSONConfiguration from which you can get the location of
	 * the deck file and the number of AI players. 
	 @param conf */
	 
public TopTrumpsRESTAPI(TopTrumpsJSONConfiguration conf) {
		
		// Add relevant initalization here
		deckFile = conf.getDeckFile();
		numPlayers = conf.getNumAIPlayers() + 1;
}
		@GET
		@Path("/helloJSONList")
		/**
		 * Here is an example of a simple REST get request that returns a String.
		 * We also illustrate here how we can convert Java objects to JSON strings.
		 * @return - List of words as JSON
		 * @throws IOException
		 */
	public String helloJSONList() throws IOException {
			
			List<String> listOfWords = new ArrayList<String>();
			listOfWords.add("Hello");
			listOfWords.add("World!");
			
			// We can turn arbatory Java objects directly into JSON strings using
			// Jackson seralization, assuming that the Java objects are not too complex.
			
			String listAsJSONString = oWriter.writeValueAsString(listOfWords);
			
			return listAsJSONString;
		}
		
	@GET
	@Path("/helloWord")
	
		 /* Here is an example of how to read parameters provided in an HTML Get request.
		 * @param Word - A word
		 * @return - A String
		 * @throws IOException */
		 
   public String helloWord(@QueryParam("Word") String Word) throws IOException {
		return "Hello "+Word;
	
		}
	
    @GET
	@Path("/setPlayers")
    @Consumes(MediaType.APPLICATION_JSON)
	
   public void setPlayers(@QueryParam("Number") int Number) throws IOException {
	  numPlayers = Number + 1; 
	   System.err.println("the number of players: " + numPlayers);
	   startGame();
    }
	// ----------------------------------------------------
	// Add relevant API methods here
	// ----------------------------------------------------
	@GET
	@Path("/activePlayer")
		public String activePlayer() throws IOException {
		//	System.err.println("Active player is " + activePlayer.getName()); testing

			if (activePlayer != humanPlayer) {
				computerSelect();
			}
	     return activePlayer.getName();
		}
		
		private void computerSelect() {
			
		}
			// TODO Auto-generated method stub
		@GET
		@Path("/StartGame")
		public void startGame() {
			File file = new File("StarCitizenDeck.txt");
			ImportDeckInformation playerDeck = new ImportDeckInformation();
			java.util.ArrayList<commandline.Card> deck;
			gameDeck.shuffleDeck(gameDeck.getDeck());

			ArrayList<Card> playerdeck = gameDeck.shuffleDeck(deck);
			this.humanPlayer = new HumanPlayer( ArrayList <Card> deck);
			players = new ArrayList<Player>();
			players.add(humanPlayer);

			ArrayList<Card> commonPile = new ArrayList<Card>();
			for (int i = 1; i < playerDeck.size(); i++) {
				Player p = new AIPlayer(playerDeck, "Computer " + i);
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
		@GET
		@Path("/selectCategory")
		public Card selectCategory(@QueryParam("Number") int Number) throws IOException {
			catIndex = Number - 1;
			Object catChoice;
			commandline.Card catString = activePlayer.getName();
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
	
						Object catChoice;
						((Object) p(catChoice)).getHeldCard(catIndex);
						// assigns the above value to each player
						p.setChosenCat(p.getName(c).output());

						p.getDeck().remove(0);
					}
				}
				compareCards();
				
				//if draw return top two players, else give top player winnerPile and clear it
				if (players.get(0).compareTo(players.get(1)) == 0) {
					String draw = "Draw between " + players.get(0).getName() + " & " + players.get(1).getName();
				String drawn;
					//	System.err.println("DRAW~~~~~~~~~~~~~");
					return drawn;
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

		private Object p(Object catChoice) {
			// TODO Auto-generated method stub
			return null;
		}
		private void compareCards() {
			// TODO Auto-generated method stub
			
		}
		@GET
		@Path("/endGame")
		public String endGame() throws JsonProcessingException {
			String gameWinnerName = "";
			if (checkDecks() == 1) {
				gameWinner = players.get(0);
				gameWinnerName = gameWinner.getName();
				saveGameStats();
				numRounds = 0;
				numDraws = 0;

				for (int i = 0; i < playerWinCounts.length; i++) {
					playerWinCounts[i] = 0;
				}
			}
			String gameWinnerString = oWriter.writeValueAsString(gameWinnerName);
			return gameWinnerString; 
		}	
		

/**
 * Method is called at the end of the round. It increments the number of
 * round wins for the winner of the round.
 * */
public void incrementPlayerWins() {
	if (winner.getName().equals(humanPlayer.getName()))
		playerWinCounts[0]++;
	else if (winner.getName().equals("Computer 1"))
		playerWinCounts[1]++;
	else if (winner.getName().equals("Computer 2"))
		playerWinCounts[2]++;
	else if (winner.getName().equals("Computer 3"))
		playerWinCounts[3]++;
	else if (winner.getName().equals("Computer 4"))
		playerWinCounts[4]++;
}

/**
 * Constructs an array of card objects to be displayed in the GUI. The cards
 * are sorted in accordance with the order of players. Human first then the computer players.
 * This is done to ensure cards are being loaded in the correct slots within the GUI.
 * 
 * @return cardArray - JSON String list of JSON objects used to represent cards
 * */
@GET
@Path("/sendCardArray")
public String sendCardArray() throws IOException {
	Card[] cards = new Card[numPlayers];

	for (int i = 0; i < numPlayers; i++) {
		if (players.get(i) != null && players.get(i).getDeckSize() > 0) {
			players.get(i).drawTopCard();
			switch (players.get(i).getName()) {
			case ("Human Player"):
				cards[0] = players.get(i).getHeldCard();
				continue;
			case ("AI 1"):
				cards[1] = players.get(i).getHeldCard();
				continue;
			case ("AI 2"):
				cards[2] = players.get(i).getHeldCard();
				continue;
			case ("AI 3"):
				cards[3] = players.get(i).getHeldCard();
				continue;
			case ("AI 4"):
				cards[4] = players.get(i).getHeldCard();
				continue;
			default:
				System.err.println("There is no player");
			}
		}
	}

	// add cards to winner pile here
	for (int i = 0; i < numPlayers; i++) {
		if (players.get(i) != null && players.get(i).getDeckSize() > 0) {
			winnerPile.add(players.get(i).getHeldCard());
		}
	}

	String cardArray = oWriter.writeValueAsString(cards);
	return cardArray;

}

/**
 * Returns the number of the active round
 * 
 * @return numRoundsString - String representation of round number
 * */
@GET
@Path("/roundNumber")
public String roundNumber() throws JsonProcessingException {

	String numRoundsString = oWriter.writeValueAsString(numRounds);
	return numRoundsString;

}

/**
 * Method to display the winner of the round
 * 
 * @return xAsJsonString - String name of the winner of the game
 */
@GET
@Path("/printWinner")
public String printWinner() throws IOException {

	String x = winner.getName();
	String xAsJsonString = oWriter.writeValueAsString(x);
	return xAsJsonString;
}

/**
 * Returns the number of cards within the communal pile
 * 
 * @return xAsJsonString - String number of cards in communal pile
 * */
@GET
@Path("/cardPile")
public String cardPile() throws IOException {
	int size = winnerPile.size();

	String xAsJsonString = oWriter.writeValueAsString(size);
	return xAsJsonString;
}

/**
 * Returns the size of the deck of each player as an array. The array is in the same
 * order as the card containers are loaded in the GUI.
 * 
 * @return handArray - String array of numbers to represent the size of each players deck.
 * */
@GET
@Path("/cardsLeft")
public String cardsLeft() throws IOException {
	int[] hands = new int[numPlayers];

	for (int i = 0; i < numPlayers; i++) {
		if (players.get(i) != null) {

			switch (players.get(i).getName()) {
			case ("Human Player"):
				hands[0] = players.get(i).getDeckSize();
				continue;
			case ("Computer 1"):
				hands[1] = players.get(i).getDeckSize();
				continue;
			case ("Computer 2"):
				hands[2] = players.get(i).getDeckSize();
				continue;
			case ("Computer 3"):
				hands[3] = players.get(i).getDeckSize();
				continue;
			case ("Computer 4"):
				hands[4] = players.get(i).getDeckSize();
				continue;
			default:
				System.err.println("One of the player names is incorrect");
			}
		}
	}

	String handArray = oWriter.writeValueAsString(hands);
	return handArray;
}

/**
 * Method to populate the saved statistics of the game
 * 
 * @return xAsJsonString - String representation of array containing pertinent information
 * game statistics
 * */
	
	@GET
	@Path("/statsTable")
	
	public String statsTable() throws IOException {
		Application db = new Application();
		int[] x = db.getGameStatisticsOnline();
		db.closeConnection();
		db = null;

		String xAsJsonString = oWriter.writeValueAsString(x);
		return xAsJsonString;
	}

	}


