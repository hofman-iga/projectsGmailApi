# projectsGmailApi

## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Installation](#installation)
* [Usage](#usage)

## General info
Java app utilizing Gmail API, enables downloading mails content and saving data in Excel file.  
\
This project is created on specific request. It is customized to search for particular key words and mail format.
  
## Technologies
Project is created with:
* Java 8
* Maven
* Gmail API
* Apache POI API

	
## Installation
To run this project, install it locally:

```
$ git clone git@github.com:hofman-iga/projectsGmailApi.git

In the directory with jar file:

$ java -jar gmailApi.jar
```

## Usage

After application is started, user is authenticated via Google account. Google API used in a project enables user authentication and authorization via OAuth 2.0 protocol.
User mailbox is searched for certain messages containing project data. User can choose number of days from which messages should be checked.
Data is extracted and using Apache POI API saved to Excel file, where list with all the projects from the month is kept. If file for certain month doesn't exist, aplication creates new one.
