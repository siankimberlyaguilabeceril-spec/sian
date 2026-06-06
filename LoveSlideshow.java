import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;

public class LoveSlideshow extends JFrame {

    // ─── Slides ───────────────────────────────────────────────────────────────
    private static final String[] MESSAGES = {
        "Hello Shin :>,\nhow are you?",

        "I know that it really hurts —\nwhatever it was that we had\nthat came to an end.\n\nEven I am hurting too...",

        "I'm sorry for not being patient with you\nand for adding to your problems\ninstead of easing them.\n\nI am really sorry. 🌸",

        "I just want you to know\nthat I am grateful that I met you,\neven if it was only for a short time.",

        "You made me realize that\nyou didn't love me only\nbecause of my dark side —\n\nhaha, thank you. 💛",

        "You made me feel so special\nduring the times that you could,\nand you never fell short in that.",

        "I am really sorry.\nAnd you know, my plan now\nis to simply move forward\nand focus on college. 💙",

        "Because that's what I truly want —\nto work hard for my family. :))",

        "I just want you to know\nthat I am always here for you\nif you ever need someone to talk to.",

        "I am here for you,\nquietly supporting you from afar,\nand I will always pray for you. 🌷",

        "I love you\nand please always\ntake care of yourself. 💕"
    };

    // ─── Colors ───────────────────────────────────────────────────────────────
    private static final Color[] SLIDE_BG = {
        new Color(255, 228, 235), // blush pink
        new Color(214, 234, 255), // soft blue
        new Color(255, 249, 210), // warm yellow
        new Color(255, 228, 235),
        new Color(214, 234, 255),
        new Color(255, 249, 210),
        new Color(255, 228, 235),
        new Color(214, 234, 255),
        new Color(255, 249, 210),
        new Color(255, 228, 235),
        new Color(214, 234, 255),
    };

    private static final Color PINK   = new Color(255, 182, 193);
    private static final Color BLUE   = new Color(173, 216, 230);
    private static final Color YELLOW = new Color(255, 223, 100);
    private static final Color DEEP_PINK = new Color(219, 112, 147);

    // ─── State ────────────────────────────────────────────────────────────────
    private int currentSlide = 0;
    private float fadeAlpha  = 1f;
    private boolean fading   = false;
    private int nextSlide    = 0;

    private SlidePanel slidePanel;
    private JLabel pageLabel;
    private Timer fadeTimer;
    private final Timer floatTimer;

    // floating hearts
    private final java.util.List<Heart> hearts = new ArrayList<>();
    private final Random rng = new Random();

    // ─── Entry point ──────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoveSlideshow().setVisible(true));
    }

    // ─── Constructor ──────────────────────────────────────────────────────────
    public LoveSlideshow() {
        setTitle("For Shin 💕");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(720, 520);
        setLocationRelativeTo(null);
        setResizable(false);

        // Spawn hearts periodically
        for (int i = 0; i < 18; i++) spawnHeart();

        slidePanel = new SlidePanel();
        slidePanel.setLayout(new BorderLayout());

        // ── Navigation bar ────────────────────────────────────────────────────
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 8));
        nav.setOpaque(false);

        JButton prev = makeNavBtn("‹ Prev");
        JButton next = makeNavBtn("Next ›");
        pageLabel = new JLabel(pageText(), SwingConstants.CENTER);
        pageLabel.setFont(new Font("Serif", Font.ITALIC, 13));
        pageLabel.setForeground(DEEP_PINK);
        pageLabel.setPreferredSize(new Dimension(90, 24));

        prev.addActionListener(e -> goTo((currentSlide - 1 + MESSAGES.length) % MESSAGES.length));
        next.addActionListener(e -> goTo((currentSlide + 1) % MESSAGES.length));

        nav.add(prev);
        nav.add(pageLabel);
        nav.add(next);

        slidePanel.add(nav, BorderLayout.SOUTH);
        add(slidePanel);

// ── Fade timer ────────────────────────────────────────────────────────
        fadeTimer = new Timer(16, e -> {
            if (fading) {
                fadeAlpha -= 0.07f;
                if (fadeAlpha <= 0f) {
                    fadeAlpha   = 0f;
                    currentSlide = nextSlide;
                    fading      = false;
                    pageLabel.setText(pageText());
                }
            } else {
                fadeAlpha = Math.min(1f, fadeAlpha + 0.07f);
                if (fadeAlpha >= 1f) fadeTimer.stop();
            }
            slidePanel.repaint();
        });

        // ── Float / heart timer ───────────────────────────────────────────────
        floatTimer = new Timer(30, e -> {
            for (Heart h : hearts) h.update();
            hearts.removeIf(h -> h.y < -30);
            while (hearts.size() < 18) spawnHeart();
            slidePanel.repaint();
        });
        floatTimer.start();
    }

    // ─── Navigation ───────────────────────────────────────────────────────────
    private void goTo(int index) {
        if (fading) return;
        nextSlide = index;
        fading    = true;
        fadeAlpha = 1f;
        if (!fadeTimer.isRunning()) fadeTimer.start();
    }

    private String pageText() {
        return (currentSlide + 1) + " / " + MESSAGES.length;
    }

     // ─── Heart spawner ────────────────────────────────────────────────────────
    private void spawnHeart() {
        hearts.add(new Heart(
            rng.nextInt(720),
            520 + rng.nextInt(100),
            6 + rng.nextInt(14),
            0.4f + rng.nextFloat() * 1.2f,
            pickHeartColor()
        ));
    }

    private Color pickHeartColor() {
        int r = rng.nextInt(3);
        return r == 0 ? PINK : r == 1 ? BLUE : YELLOW;
    }

// ─── Nav button factory ───────────────────────────────────────────────────
    private JButton makeNavBtn(String label) {
        JButton btn = new JButton(label) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? DEEP_PINK : PINK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Serif", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth()  - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(90, 32));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Slide panel
    // ═══════════════════════════════════════════════════════════════════════════
    private class SlidePanel extends JPanel {

        SlidePanel() { setPreferredSize(new Dimension(720, 520)); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,   RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();

            // ── Background gradient ───────────────────────────────────────────
            Color bg = SLIDE_BG[currentSlide % SLIDE_BG.length];
            Color bg2 = bg.brighter();
            GradientPaint gp = new GradientPaint(0, 0, bg2, w, h, bg);
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);

            // ── Soft bokeh circles ────────────────────────────────────────────
            drawBokeh(g2);

            // ── Floating hearts ───────────────────────────────────────────────
            for (Heart heart : hearts) heart.draw(g2);

            // ── Card ──────────────────────────────────────────────────────────
            int cx = 60, cy = 55, cw = w - 120, ch = h - 130;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha * 0.92f));
            g2.setColor(new Color(255, 255, 255, 210));
            g2.fillRoundRect(cx, cy, cw, ch, 40, 40);

            // card border
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha * 0.4f));
            g2.setStroke(new BasicStroke(2f));
            g2.setColor(PINK);
            g2.drawRoundRect(cx, cy, cw, ch, 40, 40);

             // ── Message text ──────────────────────────────────────────────────
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
            drawCenteredText(g2, MESSAGES[currentSlide], cx, cy, cw, ch);

            // ── Decorative corner hearts ──────────────────────────────────────
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha * 0.55f));
            drawSmallHeart(g2, cx + 18, cy + 18, 10, PINK);
            drawSmallHeart(g2, cx + cw - 18, cy + 18, 10, BLUE);
            drawSmallHeart(g2, cx + 18, cy + ch - 18, 10, YELLOW);
            drawSmallHeart(g2, cx + cw - 18, cy + ch - 18, 10, PINK);

            g2.dispose();
        }

        private void drawBokeh(Graphics2D g2) {
            int[][] circles = {
                {80,  60,  90}, {600, 80,  70}, {350, 420, 80},
                {150, 380, 60}, {560, 350, 55}, {260, 100, 50}
            };
            Color[] cols = {
                new Color(255,182,193,40), new Color(173,216,230,40),
                new Color(255,223,100,35), new Color(255,182,193,30),
                new Color(173,216,230,30), new Color(255,223,100,25)
            };
            for (int i = 0; i < circles.length; i++) {
                int[] c = circles[i];
                g2.setColor(cols[i]);
                g2.fillOval(c[0] - c[2]/2, c[1] - c[2]/2, c[2], c[2]);
            }
        }

        private void drawCenteredText(Graphics2D g2, String text, int cx, int cy, int cw, int ch) {
            String[] lines = text.split("\n");
            g2.setColor(new Color(100, 60, 80));

            // measure total block
            int lineH = 0;
            Font[] fonts = new Font[lines.length];

            for (int i = 0; i < lines.length; i++) {
                boolean isBig   = i == 0 && lines.length > 1 && !lines[0].isEmpty();
                boolean isEmpty = lines[i].trim().isEmpty();
                fonts[i] = isEmpty ? new Font("Serif", Font.PLAIN, 10)
                         : isBig  ? new Font("Serif", Font.BOLD | Font.ITALIC, 22)
                                  : new Font("Serif", Font.PLAIN, 18);
                g2.setFont(fonts[i]);
                FontMetrics fm = g2.getFontMetrics();
                // measure width for layout (not stored)
                fm.stringWidth(lines[i]);
                lineH = Math.max(lineH, fm.getHeight());
            }

            int totalH = lines.length * (lineH + 6);
            int startY = cy + (ch - totalH) / 2 + lineH;

            for (int i = 0; i < lines.length; i++) {
                g2.setFont(fonts[i]);
                FontMetrics fm = g2.getFontMetrics();
                int tx = cx + (cw - fm.stringWidth(lines[i])) / 2;
                int ty = startY + i * (lineH + 6);

                // soft shadow
                g2.setColor(new Color(220, 160, 180, 60));
                g2.drawString(lines[i], tx + 1, ty + 1);
                g2.setColor(new Color(100, 60, 80));
                g2.drawString(lines[i], tx, ty);
            }
        }

        private void drawSmallHeart(Graphics2D g2, int x, int y, int size, Color c) {
            g2.setColor(c);
            GeneralPath path = heartPath(x, y, size);
            g2.fill(path);
        }
    }

    // ─── Heart shape ──────────────────────────────────────────────────────────
    private static GeneralPath heartPath(int cx, int cy, int size) {
        double s = size / 10.0;
        GeneralPath p = new GeneralPath();
        p.moveTo(cx, cy + 3 * s);
        p.curveTo(cx, cy, cx - 5 * s, cy, cx - 5 * s, cy - 2.5 * s);
        p.curveTo(cx - 5 * s, cy - 5 * s, cx, cy - 5 * s, cx, cy - 2.5 * s);
        p.curveTo(cx, cy - 5 * s, cx + 5 * s, cy - 5 * s, cx + 5 * s, cy - 2.5 * s);
        p.curveTo(cx + 5 * s, cy, cx, cy, cx, cy + 3 * s);
        p.closePath();
        return p;
    }

// ═══════════════════════════════════════════════════════════════════════════
    // Floating Heart particle
    // ═══════════════════════════════════════════════════════════════════════════
    private static class Heart {
        int x;
        float y;
        int   size;
        float speed;
        float drift;
        float driftAngle;
        float alpha;
        Color color;
        Random rng = new Random();

        Heart(int x, float y, int size, float speed, Color color) {
            this.x = x;
            this.y = y;
            this.size  = size;
            this.speed = speed;
            this.color = color;
            this.alpha = 0.3f + rng.nextFloat() * 0.5f;
            this.drift = rng.nextFloat() * 1.5f - 0.75f;
            this.driftAngle = rng.nextFloat() * 360f;
        }

        void update() {
            y -= speed;
            driftAngle += 2f;
            x += (int)(Math.sin(Math.toRadians(driftAngle)) * drift);
        }

         void draw(Graphics2D g2) {
            Graphics2D g = (Graphics2D) g2.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g.setColor(color);
            g.fill(heartPath(x, (int) y, size));
            g.dispose();
        }
    }
}
