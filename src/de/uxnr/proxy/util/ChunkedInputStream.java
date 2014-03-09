package de.uxnr.proxy.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class ChunkedInputStream extends InputStream {
  private InputStream stream;
  private BufferedReader reader;
  private int remaining = 0;

  public ChunkedInputStream(InputStream input) throws UnsupportedEncodingException {
    this.stream = input;
    this.reader = new BufferedReader(new InputStreamReader(this.stream, "UTF-8"));
  }

  @Override
  public int available() throws IOException {
    return Math.max(this.remaining, this.stream.available());
  }

  @Override
  public int read() throws IOException {
    if (this.remaining > 0) {
      int data = this.reader.read();
      this.remaining -= 1;
      if (this.remaining == 0) {
        this.reader.readLine();
      }
      return data;
    }

    String line = this.reader.readLine();
    if (line.isEmpty()) {
      return -1;
    }

    this.remaining = Integer.parseInt(line, 16);
    return this.read();
  }

  @Override
  public void close() throws IOException {
    while (this.read() != -1);
    this.reader.close();
  }
}
