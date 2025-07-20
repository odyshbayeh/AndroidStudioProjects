package ToDo5.demo;

import ToDo5.demo.Book;
import ToDo5.demo.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @PostMapping
    public String addBook(@RequestBody Book book) {
        return bookService.addBook(book);
    }

    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Integer id, @RequestBody Book book) {
        return bookService.updateBook(id, book);
    }

    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable Integer id) {
        return bookService.deleteBook(id);
    }

    @GetMapping("/title/{title}")
    public Book getBookByTitle(@PathVariable String title) {
        return bookService.getBookByTitle(title);
    }
}
