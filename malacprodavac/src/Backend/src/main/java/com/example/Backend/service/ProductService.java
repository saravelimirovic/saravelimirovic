package com.example.Backend.service;

import com.example.Backend.dto.ProductDTO;
import com.example.Backend.dto.ProductUpdateDTO;
import com.example.Backend.entity.*;
import com.example.Backend.fileSystemImpl.FileSystemUtil;
import com.example.Backend.fileSystemImpl.enums.ImageType;
import com.example.Backend.repository.CompanyRepository;
import com.example.Backend.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Consumer;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ProductService {

    private final ProductRepository productRepository;
    private final CompanyRepository companyRepository;

    private final FileSystemUtil fileSystem;

    // add proizvod
    @Transactional
    public Product createProduct(ProductDTO param, Long userId) {
        Product newProduct = new Product();
        newProduct.setName(param.getName());

        // pretpostavljam da ima jednu kompaniju
        Company company = companyRepository.findCompaniesByUserId(userId).get(0);

        if (company == null) {
            throw new IllegalArgumentException("There is no company with this id.");
        }
        else {
            newProduct.setCompany(company);
        }
        newProduct.setDescription(param.getDescription());
        MeasuringUnit measuringUnit = MeasuringUnit.getById(param.getMeasuringUnit());
        newProduct.setMeasuringUnit(measuringUnit);
        ProductCategory productCategory = ProductCategory.getById(param.getProductCategory());
        newProduct.setProductCategory(productCategory);
        newProduct.setQuantity(param.getQuantity());
        newProduct.setPrice(param.getPrice());

        Product addedProduct = productRepository.save(newProduct);

        if(param.getImage() != null) {
            byte[] image = Base64.getDecoder().decode(param.getImage());
            fileSystem.saveImage(String.valueOf(addedProduct.getId()), image, ImageType.PRODUCT);
        }
        // ako nije uneo sliku, postavlja se ona defaultna
        else {
            byte[] defaultImg = Base64.getDecoder().decode("/9j/4AAQSkZJRgABAQEBLAEsAAD/4QBWRXhpZgAATU0AKgAAAAgABAEaAAUAAAABAAAAPgEbAAUAAAABAAAARgEoAAMAAAABAAIAAAITAAMAAAABAAEAAAAAAAAAAAEsAAAAAQAAASwAAAAB/+0ALFBob3Rvc2hvcCAzLjAAOEJJTQQEAAAAAAAPHAFaAAMbJUccAQAAAgAEAP/hDIFodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvADw/eHBhY2tldCBiZWdpbj0n77u/JyBpZD0nVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkJz8+Cjx4OnhtcG1ldGEgeG1sbnM6eD0nYWRvYmU6bnM6bWV0YS8nIHg6eG1wdGs9J0ltYWdlOjpFeGlmVG9vbCAxMC4xMCc+CjxyZGY6UkRGIHhtbG5zOnJkZj0naHR0cDovL3d3dy53My5vcmcvMTk5OS8wMi8yMi1yZGYtc3ludGF4LW5zIyc+CgogPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9JycKICB4bWxuczp0aWZmPSdodHRwOi8vbnMuYWRvYmUuY29tL3RpZmYvMS4wLyc+CiAgPHRpZmY6UmVzb2x1dGlvblVuaXQ+MjwvdGlmZjpSZXNvbHV0aW9uVW5pdD4KICA8dGlmZjpYUmVzb2x1dGlvbj4zMDAvMTwvdGlmZjpYUmVzb2x1dGlvbj4KICA8dGlmZjpZUmVzb2x1dGlvbj4zMDAvMTwvdGlmZjpZUmVzb2x1dGlvbj4KIDwvcmRmOkRlc2NyaXB0aW9uPgoKIDxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PScnCiAgeG1sbnM6eG1wTU09J2h0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8nPgogIDx4bXBNTTpEb2N1bWVudElEPmFkb2JlOmRvY2lkOnN0b2NrOjM5NjJlODU0LTYwNTQtNDBmZi1hMGMzLWIyZDY3MDM0NjlhYTwveG1wTU06RG9jdW1lbnRJRD4KICA8eG1wTU06SW5zdGFuY2VJRD54bXAuaWlkOmE5M2NiZTgyLWI5NjEtNGUzNS1iYmQ0LWJjZWE1Zjc1YTQwZjwveG1wTU06SW5zdGFuY2VJRD4KIDwvcmRmOkRlc2NyaXB0aW9uPgo8L3JkZjpSREY+CjwveDp4bXBtZXRhPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAo8P3hwYWNrZXQgZW5kPSd3Jz8+/9sAQwAFAwQEBAMFBAQEBQUFBgcMCAcHBwcPCwsJDBEPEhIRDxERExYcFxMUGhURERghGBodHR8fHxMXIiQiHiQcHh8e/8AACwgBaAFoAQERAP/EABwAAQACAwEBAQAAAAAAAAAAAAAFBgEDBAIHCP/EAEEQAAEDAgEFDAkDBAIDAQAAAAABAgMEBREGEiExYRQWIjVBUVRxkrGy0RMVMlNjgpGT4YHB8CM2QlJDoTM0cnT/2gAIAQEAAD8A/ZYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABFX+8R22HMZg+oenAZzbV2HPkpc6muikjqGOcsf/ADYYIuxdpOgAAAAAAAAAAAAAAAAAAAAisoLxHbYcxmD6h6cBvNtXYVa10FVeq50kj3ZuOMsq9ybS8UdNDSU7YIGIxjU0J+5uAAAAAAAAAAAAAAAAAAAAIrKC8R22HMZg+pcnBbzbV2FWtdBVXmudJI92bnYyyr3JtLxR00NJTtggYjGNTQhuAAAAAAAAAAAOSoudBTzOhmq4o5G62udpQ1+ubX0+DtD1za+nwdoeubX0+DtD1za+nwdoeubX0+DtD1za+nwdoeubX0+DtD1za+nwdoeubX0+DtD1za+nwdoeubX0+DtD1za+nwdoeubX0+DtD1za+nwdoeubX0+DtD1za+nwdo9R3a2ySNjjrIXvcuDWo7FVU57/AHiO2w5jMH1L04LObauwqttoam8Vj5ZZFRiLnTTO5Pz3Fqo7hZaSnbBBWU7GN0Imd/2bvXNr6fB2jZT3OgqJmww1cUkjtTWu0qdYAAAAAAAAAAKFlXx/UfL4UO1MlKxURd1QfRRvUrOlQfRw3qVnSoPo4b1KzpUH0cN6lZ0qD6OG9Ss6VB9HDepWdKg+jhvUrOlQfRw3qVnSoPo4b1KzpUH0cN6lZ0qD6OG9Ss6VB9HDepWdKg+jhvUrOlQfRw3qVnSoPo4b1KzpUH0cbMynycgVznMqLjIi5nMxOf8AmsjbXQVV6rnSSPdm52Msq9ybSyXO01MlGygoJYaelROEi45z12/zSRO9Ss6VB9HBclKxEVd1QfRTiyU4/p/m8Kl9AAAAAAAAAABQsquP6j5fChfG+ynUZI683emtjE9JjJK5MWxt1rtXmQrk2VFwc/GNkEbeRM1Xf94nXbsqnZ6MroWo1f8AOPHR1oWeKRksbZI3I9jkxRUXQqHoifXUXr71dozMM3P+JzfzlJYAAAisoLxHbYcxmD6h6cBvNtXYVa10FVeq50kj3ZuOMsq9ybS8UdNDSU7YIGIxjU0IbgYd7K9RQ8leP6f5vCpfQAAAAAAAAAAULKrj+o+XwoXxvsp1GJXtjidI72Woqr1IfNq2pkrKqSplXF0i49SciGkFpyGrHL6aheuLWp6RmzTgqdxM32vS3298yKnpF4Mac7l8tZ8+z35/pM5c/HOzsdOPOfQbFXpcLeyZVT0icGROZyees7wACKygvEdthzGYPqHpwG821dhVrXQVV6rnSSPdm44yyr3JtLxR00NJTtggYjGNTQhuAMO9leooeSvH9P8AN4VL6AAAAAAAAAAChZVcf1Hy+FC+N9lOo11say0c0Tdb43NT9UPmeCpoVMFTQoBP5DxOdcppcFzWRYKu1VTyU5cp7hu64q1jsYYcWs2ryr/OYiiVyYuG4bijXuwhmwa/YvIv85y+AAisoLxHbYcxmD6l6cBvNtXYVa10FVeq50kj3ZuOMsq9ybS8UdNDSU7YIGIxjdSG4AGHeyvUUPJXj+n+bwqX0AAAAAAAAAAFCyq4/qPl8KF8b7KdRkqeUtil9O+soo1e164yRt1ovOnOVpyK1VR2hU1oug6KGiqq2VI6aJz+d3+KdaliuCR2Cx7lhfnVVRjnPTXtX9NSFVALzkpcN229IpHYzQYNdjrVORf5zEwCKygvEdthzGYPqHpwWc21dhVrXQVV6rnSSPdm44yyr3JtLxR00NJTtggYjGNTQn7m4AAw72V6ih5K8f0/zeFS+gAAAAAAAAAAoWVXH9R8vhQvjfZTqMg1yQQyLjJDG9edzUU9L6OGJV4LGNTFeREQ+eXqudcLhJULjmezGnM1NXmcYB2WWudb7hHUJjmezInO1dfmfRGOa9jXsVFa5MUVOVCLv94jt0OYzB9Q9OAzm2rsKta6CqvVc6SR7s3OxllXuTaXijpoaSnbBAxGMamhP3NwAAMO9leooeSvH9P83hUvoAAAAAAAAAAKFlVx/UfL4UL432U6jIBXMtLh6KnbQRO4cqYyYcjeb9SogA9RsfJI2ONque5cGtRNKqWx1c+xWaKlmkbLWKiqxiamIvPsQhLXQVV6rnSSPdm44yyr3JtLxR00NJTtggYjGNTQn7m4AAAw72V6ih5K8f0/zeFS+gAAAAAAAAAAoWVXH9R8vhQvjfZTqMg1Vc8dLTSVEq4Mjbip85rqmSrq5KmVeE92OHMnIhpAPUbHySNjjar3uXBrUTSqlhY2DJ2mSSRGTXKVvBbrSNP59SOttDU3isfLLIqMRc6aZ3Js6+4u1rZSMoY0os1YMOCreXadIBTsp7y+WubDSSK1lO/HOavtPT9kLHZLgy40LZm4I9ODI3/V3kdwBh3sr1FDyV4/p/m8Kl9AAAAAAAAAABQsquP6j5fChfG+ynUZBUstbhnytt8TuCzhS4cq8ifuVsA9RsfJI2ONqve5cGtRMVVSwsbBk7TekkRk1ylbwW60jT+fUjrbRVN4rHyyyKjEXOmmdyfnuN15ucSwJbbano6Nmhzk1yL5d52ZF3DMldb5XcF+LoseReVP11ltBB5V3XcdNuaB39eVNaf4N5+spR3WO4vtta2VMVidwZG86c/Wh9BikZLE2SNyOY5MWqnKh6Bh3sr1FDyV4/p/m8Kl9AAAAAAAAAABQsquP6j5fChfG+ynUZOS71rKCgkqHYKqJgxOdy6kPncr3yyOkkcrnvVXOVeVVPIMxsfJI2ONqve5cGtRNKqWJjYMnaVJJEZNcpW8FutI0/n1I620NTeKx8ssjkZjnTTO5NnX3G683OJYEtttT0dGzQ5ya5F8u8hz1E98UjZI3K17VRzVTkVD6JaK1tfQR1LdCqmD05nJrQ9XStioKN9TLqTQ1vK5eRD55V1EtVUvqJnZz3rivkagWTI+6+jelvndwHL/AElXkX/X9S2gw72V6ih5K8f0/wA3hUvoAAAAAAAAAAKFlVx/UfL4UL432U6jJSMrrhuuv3PG7GGBVTRyu5V/b6kKDMbHySNjjarnuXBrUTSqliY2DJ2m9JIjJrlK3gt1pGn8+pHW2hqbxWPllkVGY500zuTZ19xuvNziWBLbbU9HRs0OcmuRfLvIcAmsk7ilHXLBK5Ehm0Kqroa7kX9jTlHc1uNZwFXc8eKRpz87v1IsAIqoqKiqipqVC95NXRLhR5sipuiLQ9P9uZxLGHeyvUUPJXj+n+bwqX0AAAAAAAAAAFCyq4/qPl8KF8b7KdRG5SXDcFucrFwmk4MexeVf0KCD1Gx8kjY42q97lwa1E0qpYWNgydpUkkRk1ylbwW60jT+fUjrbRVN4rHyyyKjEXOmmdybOvuN15ucSwpbbano6Nmhzk1yL5d5DgAAAA6LbWS0NYyph1t1pyOTlQ+h0VTFV0rKiF2LHpimzYbXeyvUUPJXj+n+bwqX0AAAAAAAAAAFCyq4/qPl8KF8RURiKq4IiHz/KC4LcLi+Rq/0mcCPq5/1I8zGx8kjY42q97lwa1E0qpYmNgydpkkkRk1ylbwW60jT+fUjrbQ1N4rHyyyKjMc6aZ3J+e43Xm5xLAlttqejo2aHOTXIvl3kOAAAAACayVum4qrc8zsKeVda/4O5+rnLs72V6ih5K8f0/zeFS+gAAAAAAAAAAoWVXH9R8vhQu9RC2ppHQPc9rXtzVVq4LgRO9i2fH+5+BvYtnx/ufg5a1Lbk9i+mYslY9uDEe7OzE59n7kPa6CqvVc6SR7s3HGWVe5NpcJLXTPt6ULfSRQJrSN2Cu615Th3sWz4/3PwN7Fs+P9z8DexbPj/c/A3sWz4/3PwN7Fs+P9z8DexbPj/c/A3sWz4/3PwN7Fs+P9z8DexbPj/c/A3sWz4/3PwN7Fs+P9z8DexbPj/c/A3sWz4/3PwN7Fs+P9z8EvBEkFK2FrnuRjcEV64rh1lHyV4/p/m8Kl9AAAAAAAAAABTMpLdXz3meWGklkYubg5qaF4KHJuG+e5re0vmNxXz3Nb2l8yU9L6go86eZ1RcZW6GOeqtjT+fUi7XQVV5rnSSPdm44yyr3JtJS7tuDYkt9roaiKlj0K9qYLIvPjzd5F7ivnua3tL5jcV89zW9pfMbivnua3tL5jcV89zW9pfMbivnua3tL5jcV89zW9pfMbivnua3tL5jcV89zW9pfMbivnua3tL5jcV89zW9pfMbivnua3tL5jcV89zW9pfMbivnua3tL5jcV89zW9pfMbivnua3tL5jcN89zW9pfM68m7dXwXmCWakljYmdi5yaE4KlzAAAAAAAAAAABFZQXeK2w5jM19Q9OA3m2rsKta6CqvVc6SR7s3HGWVe5NpeKOmhpKdsEDEYxqaE/c3AAAAAAAAAAAAAAAAAAAAEVlBeI7bDmMwfUuTgt5tq7CrWugqrzXOkke7NzsZZV7k2l4o6aGkp2wQMRjGpoQ3AAAAAAAAAAAAAAAAAAAAEVlBd47bDmNwfUPTgN5E2rsKta6CqvNc6SR7s3HGWVe5NpeKOmhpKdsEDEYxqaE/c3AAAAAAAAAAAAAAAAAAAAA4rvbKe5QJHMitc1cWvbrab6OmhpKdsEDEYxqaE/c3AAAAAAAAAAAAAAAAAAAAAAAGupnhpoXTTyNjjbrcupD21yOajmqitVMUVOU1uqYG1TaZZGpM5ucjOVU5zaDzI9scbpHuRrWoquVeRDxSVMFXAk9PIkka6lQ2gAAAAAAAAAAAAAAAAAAicrtFgqF2t8SHPZ55LbVNtVW5XRSJnUsi8qf6/wA8jbU/3dS//ld3m6e6SurJKSgo1qnxf+RyvRrWrzY85sttxSqnlpZoHU9TFpdG5cdHOi8qHXUq1KeRXNR7cxcWryphqImmuMFNk2yvgo2xx46Imu1Yuw14HqW8zQKyeot8kVE92CSq9M5MdSq3kQxJep4UjqKi2yRUcjkRJFkRXIi6lVvIdtwrJoHRx09HLUySY4Zq4NTDndyGqguUkta6iq6VaaoRueiZ6ORzdimtbnUzTzMoKHdDIXZj3ulRiK5NaJznu33ZtTBNUSU8lPDDjnve5NCprTBNJpS7Vr4N1x2p7qXDORyyoj1bzo0k6WeKqpo6iF2dHI3OaptAAAAAAAAAAAAAABE5XcQVHW3xIb7jQR3C2thcua9ER0b+VrsNZC2qoqJspII6xitqIIHxyL/tpxRf1Q7cm3Nhq7jSzKjZ90K/BdbmrqUMc2oyvV8Co5sFPmyuTViq6EJes/8AUm/+HdxWHf2Cz9PGSWVH9tv6md6DKn+25Pk70PdTWVLq+C20jo4nuh9I6WRM7BOZE5VOSBJG5WQslrEqntp3I5c1G5uzBDTFVLSXGvgp62mpWLMq5lSi44rrc3VoOmopI0yWq4qSdKp78ZHvaqLnOxRV1dWo92+Cnnt0c7btWNjzExT0yIjdGlNWgkrTDTQW+KOkkWSBEVWOzsccVx1nUAAAAAAAAAAAAAADVVU8NVA6CojSSN2tq8ptREREREwRDStLTrVpVLE307W5qP5cOY11tuoq1yOqaZkjk1OXQv1Q2UlLT0kXo6aFkTNeDU1m17Uc1WuTFFTBUOdaCjWhSi9A3c6f8enDXj3nuppoKmnWnnjR8S4cFdWjUZqaaCpp1gnjR8S4YtXVoNVZb6KrzN0U7ZMzQ1dKKn6oZgoKOB8b4aeNjo0VrFamGCLrIeljqKNJIqu0urXukc707c12fjqxx0oddioZIKiqq5IGUqTqmbAxcc1E5Vw0Yqb5LLa5JVlfRRK5VxXRgi/pqO5jGsYjGNRrUTBERMEQyAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAD/2Q==");
            fileSystem.saveImage(String.valueOf(addedProduct.getId()), defaultImg, ImageType.PRODUCT);
        }

        return addedProduct;
    }

    // daje proizvode po kategoriji, ali od svih proizvoda
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(String paramCategory) {
        ProductCategory productCategory = ProductCategory.getById(paramCategory);

        return productRepository.findByProductCategory(productCategory);
    }

    // daje info o jednom proizvodu
    @Transactional(readOnly = true)
    public Product getProductDetails(Long paramProductId, Consumer<Product> init) {
        Optional<Product> productDetails = productRepository.findProductById(paramProductId);

        if (init != null) {
            productDetails.ifPresent(init);
        }

        return productDetails.orElse(null);
    }


    @Transactional
    public Long deleteProductById(Long productId) {
        Long result = productRepository.deleteProductById(productId);

        return result;
    }

    @Modifying
    public Product updateProduct(ProductUpdateDTO productObj) {

        Product product = productRepository.findProductById(productObj.getProductId()).orElse(null);

        if(product == null)
            throw new IllegalArgumentException("There is no product with this id.");

        if(productObj.getProductName() != null)
            product.setName(productObj.getProductName());
        if(productObj.getProductCategory() != null) {
            ProductCategory productCategory = ProductCategory.getById(productObj.getProductCategory());
            product.setProductCategory(productCategory);
        }
        if(productObj.getProductDescription() != null)
            product.setDescription(productObj.getProductDescription());
        if(productObj.getProductPrice() != null)
            product.setPrice(productObj.getProductPrice());
        if(productObj.getQuantity() != null)
            product.setQuantity(productObj.getQuantity());
        if(productObj.getMeasuringUnit() != null) {
            MeasuringUnit measuringUnit = MeasuringUnit.getById(productObj.getMeasuringUnit());
            product.setMeasuringUnit(measuringUnit);
        }

        return productRepository.save(product);
    }


    @Transactional(readOnly = true)
    public List<Product> getSearchedProducts(String search, Consumer<Product> init) {
        List<Product> result = productRepository.findProductsByNameContainingIgnoreCase(search);

        if (init != null) {
            result.forEach(init);
        }

        return result;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
