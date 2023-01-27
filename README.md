# Advertising Service
## Basic Information
Project (a web app) of a simplified advertising service. It allows users to create accounts and then offers with information such as: title, price, description and title. Users can communicate with each other using chat, they can block each other or report other users/offers. There is also an admin role which has the possibility of punishing users and removing posts.
## Technologies and frameworks used
Mostly back-end oriented. Java 17, Spring Boot, Spring Secutiy, JPA (with Hibernate implementation), mySQL, Thymeleaf, Lombok, HTML, CSS (mainly Bootstrap). Project has been created using spring initialzr with Maven as a wrapper. Eclipse IDE has been mainly used.
## Entity-relationship diagram
![image](https://user-images.githubusercontent.com/106389146/215003020-096e5ea3-fe99-4ceb-bb34-4112baf1cd6f.png)
## How to use
Process for IDE usage: 
Download the project entirely as a **.zip** and then import it to your IDE as a Maven project (more specifically, **aservice** directory). 
The next step is configuring the database. In downloaded **.zip** pick the **sql_files** directory and choose one of the available **.sql** files.
- dbschemafinal.sql is an empty database with basic index choice
- dbschemafinalmoreindexes.sql is also an empty database with some more indexes for performance sake
- PrePopulatedDataNoindexes.sql is an prepopulated database without additional indexes, just for try-out/clicking purposes. Images haven't been provided since I do not own any of them. There is one user with an admin role with both username and password "admin". Any other account is a basic user account with the same username and password (encoded in database). For example: "MarkR".

After making a choice, set up mySQL connection and import the **.sql** file. MySQLWorkbench and Xampp were used in the project. The next step is changing **.properties** file. It is located in aservice/src/main/resources. Here it is needed to change the information under # JDBC properties section for your DB configuration. You might need to change the port of DB connection from 3306 to your own port (if you do not use the default configuration) as well as username&password for DB server connection. By default it is root without any password. Lombok is also required.

![image](https://user-images.githubusercontent.com/106389146/215005908-da55f92f-49b2-464d-a8c4-63da0a26a130.png)

Once you are done, simply run the app **AserviceApplication.java** as an Java app and the app should be working! You can then use it via your browser by connecting to localhost.

## Quick app display
After logging in to the account created via registration form or pre-created (prepopulated **.sql** import), main menu appears:

![image](https://user-images.githubusercontent.com/106389146/215152858-58389826-23a3-41c5-9014-fe8aa17203ac.png)

From here, user might go to his panel for account related options:

<img src="https://user-images.githubusercontent.com/106389146/215153037-dadf3a0c-0957-4d0c-9214-6d4e31e684e6.png" width="465">

Create his offer:

![image](https://user-images.githubusercontent.com/106389146/215153113-d9b503f8-a25d-42c9-80c3-5fb6a4e7376d.png)

Or view other user's offers:

![image](https://user-images.githubusercontent.com/106389146/215153202-74a0584c-4bac-4f81-9072-e7de2ea8d81f.png)

To communicate with other users, to get more of the offer details there is a chat function included. To begin chatting user has to pick offer and go to the other user's profile. Example of how to reach user's profile:

![image](https://user-images.githubusercontent.com/106389146/215153854-23979c5c-92e3-4ffc-a254-f123f9b4a77f.png)

After reaching their profile, there will be "Message user" button. After clicking it, chat endpoint will be reached:

![image](https://user-images.githubusercontent.com/106389146/215154146-fbe2dd65-4d63-47d1-a069-e2ca3d7833f4.png)

## Other information & known issues
This app saves images (while adding offer) locally instead of using database to increase performance. Because of that, IDE needs to support auto content refreshing to make them visible.
In Eclipse it is being set up by moving to: **Window->Preferences->Workspace->"Refresh using native hooks or polling"**. In IntellijIdea it is also required to set the correct refreshment settings in project run configuration.
Images also might not be displayed depending on that, how the project is gonna be imported since absolute path is being used. Make sure that **"aservice"** is being top directory in project explorer

![image](https://user-images.githubusercontent.com/106389146/215160780-f56a258d-2351-4405-9383-3b779608f457.png)



