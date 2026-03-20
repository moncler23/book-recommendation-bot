# book-recommendation-bot
A Java-based Telegram bot that suggests books using Data Structures and Algorithms (DSA) and the Google Books API.
Hi there! I'm Abdukodir, a Software Engineering student at Bahcesehir University. I built this project because I wanted to solve a personal problem: spending more time looking for a book than actually reading it. This is a conversational Telegram bot that acts as a digital librarian, helping users discover their next favorite read using real-time data from the Google Books API.

Why I Built ThisWhile I'm specializing in the Java/Spring ecosystem, I wanted to push myself to handle:Complex API Integrations: Fetching and parsing live data from Google Books.User Experience: Creating a bot that feels natural to talk to via the Telegram API.Efficient Logic: Using Data Structures and Algorithms (DSA) to ensure recommendations are fast and relevant.

How it Works (The "Human" Version)The Conversation: You tell the bot what you're in the mood for (e.g., "I love mystery and space").The Logic: Behind the scenes, my Java backend extracts keywords and queries the API.The Memory: I implemented a SQL database to keep track of user sessions so the bot doesn't "forget" what you liked two minutes ago.The Reliability: Because I'm dedicated to SOLID principles, the code is modular and easy to update. I even included JUnit tests to make sure the recommendation engine doesn't break when a user enters something unexpected.
Tech Stack I UsedCore: Java (My primary language) Database: SQL for interaction logs Tools: Maven for dependency management Safety: Spring Security concepts to keep things structured.

SetupIf you want to try it out yourself:Clone the repository.Get your API keys from the Telegram BotFather and Google Developers Console.Add them to your application.properties.Run it and start reading!
