# CovidTracker - A COVID Database

This program is the final group project of CIT-594: Data Structures and Software Design.

It supports reading, parsing, and storing data in memory.

Users can query different data through the program.


## Load Data
This program can run without proper data files. 

It will handle this situation gracefully.

However, some functions may not be available if the data source is missing.

Use these arguments to set the data files:


```
--population=data/population.csv

--covid=data/covid_data.json

--properties=data/properties_simple.csv

--log=log.txt
```

## Fetures

1. Show the available actions
2. Show the total population for all ZIP Codes
3. Show the total vaccinations per capita for each ZIP Code for the specified date
4. Show the average market value for properties in a specified ZIP Code
5. Show the average total livable area for properties in a specified ZIP Code
7. Show the avarage livable area of fully vaccinated people (additional feature)

## Data Sources

The OpenDataPhilly portal offers, for free, more than 300 data sets, applications, and APIs related
to the city of Philadelphia. This resource enables government officials, researchers, and the general
public to gain a deeper understanding of what is happening in our fair city. The available data sets
cover topics such as the environment, real estate, health and human services, transportation, and
public safety. The United States Census Bureau publishes similar information (and much more)
for the nation as a whole.

The course-provided files containing data from theses sources:

• “COVID” data, from the Philadelphia Department of Public Health

• “Properties” data (information about land parcels in the city), from the Philadelphia Office of
Property Assessment

• 2020 populations of Philadelphia ZIP Codes, from the US Census Bureau
