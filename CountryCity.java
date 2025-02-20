import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

class Country {
    String code, name, continent;
    double surfaceArea, gnp;
    int population, capital;

    public Country(String code, String name, String continent, double surfaceArea, int population, double gnp, int capital) {
        this.code = code;
        this.name = name;
        this.continent = continent;
        this.surfaceArea = surfaceArea;
        this.population = population;
        this.gnp = gnp;
        this.capital = capital;
    }
}

class City {
    int id, population;
    String name, countryCode;

    public City(int id, String name, int population, String countryCode) {
        this.id = id;
        this.name = name;
        this.population = population;
        this.countryCode = countryCode;
    }
}

public class CountryCity {
    public static void main(String[] args) {
        List<Country> countries = readCountriesFromCSV("Countries.csv");
        List<City> cities = readCitiesFromCSV("Cities.csv");

        findHighestPopulatedCityOfEachCountry(cities);
        findMostPopulatedCountryOfEachContinent(countries);
        findHighestPopulatedCapitalCity(countries, cities);
    }

    private static List<Country> readCountriesFromCSV(String fileName) {
        List<Country> countries = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine(); 
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
    
                try {
                    String code = data[0];
                    String name = data[1];
                    String continent = data[2];
                    double surfaceArea = Double.parseDouble(data[3]); 
                    int population = Integer.parseInt(data[4]); 
                    double gnp = Double.parseDouble(data[5]); 
                    int capital = (data[6].isEmpty() || !data[6].matches("\\d+")) ? 0 : Integer.parseInt(data[6]); 
                    
                    countries.add(new Country(code, name, continent, surfaceArea, population, gnp, capital));
                } catch (NumberFormatException e) {
                    System.err.println("ُerror " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return countries;
    }
    

    private static List<City> readCitiesFromCSV(String fileName) {
        List<City> cities = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                cities.add(new City(Integer.parseInt(data[0]), data[1], Integer.parseInt(data[2]), data[3]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cities;
    }

    private static void findHighestPopulatedCityOfEachCountry(List<City> cities) {
        Map<String, City> highestCity = cities.stream()
            .collect(Collectors.toMap(
                city -> city.countryCode,
                city -> city,
                (c1, c2) -> c1.population > c2.population ? c1 : c2
            ));

        System.out.println("Highest populated city of each country:");
        highestCity.forEach((country, city) -> System.out.println(country + " -> " + city.name + " (" + city.population + ")"));
    }

    private static void findMostPopulatedCountryOfEachContinent(List<Country> countries) {
        Map<String, Country> mostPopulatedCountry = countries.stream()
            .collect(Collectors.toMap(
                country -> country.continent,
                country -> country,
                (c1, c2) -> c1.population > c2.population ? c1 : c2
            ));

        System.out.println("\nMost populated country of each continent:");
        mostPopulatedCountry.forEach((continent, country) ->
            System.out.println(continent + " -> " + country.name + " (" + country.population + ")"));
    }

    private static void findHighestPopulatedCapitalCity(List<Country> countries, List<City> cities) {
        Map<Integer, City> cityMap = cities.stream().collect(Collectors.toMap(city -> city.id, city -> city));

        City highestCapital = countries.stream()
            .map(country -> cityMap.get(country.capital))
            .filter(Objects::nonNull)
            .max(Comparator.comparingInt(city -> city.population))
            .orElse(null);

        System.out.println("\nHighest populated capital city:");
        if (highestCapital != null) {
            System.out.println(highestCapital.name + " (" + highestCapital.population + ")");
        } else {
            System.out.println("No capital cities found.");
        }
    }
}
