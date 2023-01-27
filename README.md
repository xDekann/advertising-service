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

# Short app description <TO DO>


