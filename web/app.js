(() => {
  const STORAGE_KEY = "picapress_web_progress_v1";
  const progress = loadProgress();
  let softwareMode = "INDESIGN"; // "INDESIGN" | "QUARK"

  function loadProgress() {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      if (raw) return JSON.parse(raw);
    } catch (_) {}
    return { completedLabs: [], bestQuiz: 0, certs: 0 };
  }

  function saveProgress() {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(progress));
    } catch (_) {}
    updateStatsUI();
  }

  function markLabComplete(id, isCert = false) {
    if (!progress.completedLabs.includes(id)) {
      progress.completedLabs.push(id);
      if (isCert) progress.certs += 1;
      saveProgress();
    }
  }

  function showToast(msg) {
    const el = document.getElementById("studioToast");
    el.textContent = msg;
    el.classList.add("show");
    clearTimeout(showToast._t);
    showToast._t = setTimeout(() => el.classList.remove("show"), 3200);
  }

  function updateStatsUI() {
    document.getElementById("statCompleted").textContent = progress.completedLabs.length;
    document.getElementById("statQuiz").textContent = `${progress.bestQuiz}%`;
    document.getElementById("statCerts").textContent = progress.certs;
    const rank =
      progress.completedLabs.length >= 6
        ? "Master Swiss Typographer"
        : progress.completedLabs.length >= 3
        ? "Prepress Specialist"
        : progress.completedLabs.length >= 1
        ? "Pasteboard Compositor"
        : "DTP Apprentice";
    document.getElementById("rankBadge").textContent = `Rank: ${rank}`;
  }

  // ==================== NAVIGATION & SOFTWARE MODE ====================
  function switchTab(tabId) {
    document.querySelectorAll(".nav-btn").forEach((b) => b.classList.toggle("active", b.dataset.tab === tabId));
    document.querySelectorAll(".tab-panel").forEach((p) => p.classList.toggle("active", p.id === `tab-${tabId}`));
  }

  function switchSub(groupSub) {
    const [tab, sub] = groupSub.split(":");
    switchTab(tab);
    const parent = document.getElementById(`tab-${tab}`);
    if (!parent) return;
    parent.querySelectorAll(".sub-btn").forEach((b) => b.classList.toggle("active", b.dataset.sub === groupSub));
    parent.querySelectorAll(".sub-panel").forEach((p) => p.classList.toggle("active", p.id === `sub-${tab}-${sub}`));
  }

  document.querySelectorAll(".nav-btn").forEach((btn) =>
    btn.addEventListener("click", () => switchTab(btn.dataset.tab))
  );
  document.querySelectorAll(".sub-btn").forEach((btn) =>
    btn.addEventListener("click", () => switchSub(btn.dataset.sub))
  );
  document.querySelectorAll("[data-jump]").forEach((btn) =>
    btn.addEventListener("click", () => switchSub(btn.dataset.jump))
  );

  const swBtn = document.getElementById("softwareToggleBtn");
  swBtn.addEventListener("click", () => {
    softwareMode = softwareMode === "INDESIGN" ? "QUARK" : "INDESIGN";
    const isId = softwareMode === "INDESIGN";
    swBtn.textContent = isId ? "Id • InDesign Mode ⇄" : "QXP • QuarkXPress Mode ⇄";
    swBtn.classList.toggle("quark", !isId);
    document.getElementById("wbWrapLabel").textContent = isId
      ? "Enable Text Wrap (InDesign) around Hero Frame"
      : "Enable Runaround (QuarkXPress) around Hero Box";
    document.getElementById("pdfDialogTitle").textContent = isId
      ? "Export Adobe PDF — Live Press Sheet"
      : "Export Layout as PDF — Live Press Sheet";
    document.getElementById("pdfChromeTitle").textContent = isId
      ? "Export Adobe PDF"
      : "Export as PDF (QuarkXPress)";
    renderHierarchy();
  });

  // ==================== 1. PICA CONVERTER ====================
  const picaSlider = document.getElementById("picaSlider");
  function updatePica() {
    const val = parseFloat(picaSlider.value);
    const whole = Math.floor(val);
    const remPts = Math.round((val - whole) * 12);
    const totalPts = Math.round(val * 12);
    const inches = val / 6;
    const mm = inches * 25.4;
    document.getElementById("picaNotation").textContent = `${whole}p${remPts}`;
    document.getElementById("picaPoints").textContent = `${totalPts} pt`;
    document.getElementById("picaMetric").textContent = `${inches.toFixed(2)}" • ${mm.toFixed(1)} mm`;
  }
  picaSlider.addEventListener("input", updatePica);
  updatePica();

  // ==================== 2A. SPREAD WORKBENCH ====================
  let wbClean = false;
  function renderWorkbench() {
    const cols = +document.getElementById("wbCols").value;
    const gutter = +document.getElementById("wbGutter").value;
    const margin = +document.getElementById("wbMargin").value;
    const bleed = +document.getElementById("wbBleed").value;
    const showBaseline = document.getElementById("wbBaseline").checked;
    const wrap = document.getElementById("wbTextWrap").checked;

    document.getElementById("wbColsVal").textContent = cols;
    document.getElementById("wbGutterVal").textContent = `${Math.floor(gutter / 12)}p${gutter % 12} (${gutter}pt)`;
    document.getElementById("wbMarginVal").textContent = `${Math.floor(margin / 12)}p${margin % 12} (${margin}pt)`;
    document.getElementById("wbBleedVal").textContent = `${bleed}.0 mm`;

    const bleedPx = bleed * 2.5;
    const guideBorder = wbClean ? "none" : "1px solid rgba(236,38,143,0.75)";
    const colBg = wbClean ? "transparent" : "rgba(0,184,230,0.08)";
    const baseBg =
      showBaseline && !wbClean
        ? "repeating-linear-gradient(to bottom, transparent, transparent 11px, rgba(56,189,248,0.28) 12px)"
        : "none";

    const buildPage = (isRight) => {
      const colItems = Array.from({ length: cols })
        .map(
          (_, i) => `
          <div style="background:${colBg};border:${wbClean ? "none" : "1px dashed rgba(0,184,230,0.45)"};padding:4px;font-size:8px;line-height:1.45;color:#222;">
            ${
              isRight && i === 0
                ? `<div style="height:46px;background:linear-gradient(135deg,#143c54,#ec268f);border-radius:3px;margin-bottom:${wrap ? "8px" : "2px"};display:grid;place-items:center;color:#fff;font-weight:700;font-size:7px;">HERO FRAME</div>`
                : ""
            }
            <div>${isRight ? "Typography aligns to the baseline grid for clean facing-page rhythm." : "Columns and gutters establish optical order across print spreads."}</div>
          </div>`
        )
        .join("");

      return `
        <div class="spread-page" style="padding:${Math.round(margin * 0.55)}px;background-image:${baseBg};">
          <div style="height:100%;border:${guideBorder};display:grid;grid-template-columns:repeat(${cols},1fr);gap:${Math.round(gutter * 0.6)}px;">
            ${colItems}
          </div>
        </div>`;
    };

    document.getElementById("wbSpreadStage").innerHTML = `
      <div style="padding:${bleedPx}px;border:${bleed > 0 && !wbClean ? "1.5px solid #ff4d4d" : "none"};background:${bleed > 0 ? "rgba(255,77,77,0.12)" : "transparent"};width:100%;max-width:575px;">
        <div class="spread-sheet">${buildPage(false)}${buildPage(true)}</div>
      </div>`;
  }

  ["wbCols", "wbGutter", "wbMargin", "wbBleed", "wbBaseline", "wbTextWrap"].forEach((id) =>
    document.getElementById(id).addEventListener("input", renderWorkbench)
  );
  document.getElementById("wbPreviewToggle").addEventListener("click", (e) => {
    wbClean = !wbClean;
    e.target.textContent = wbClean ? "Show Guides (W)" : "Clean Preview (W)";
    renderWorkbench();
  });
  renderWorkbench();

  // ==================== 2B. MASTER PAGES SIMULATOR ====================
  function formatFolio(num, style) {
    if (style === "padded") return String(num).padStart(2, "0");
    if (style === "roman") return ["", "I", "II", "III", "IV", "V", "VI"][num] || num;
    return String(num);
  }

  function renderMasters() {
    const header = document.getElementById("masterHeaderInput").value;
    const footer = document.getElementById("masterFooterInput").value;
    const style = document.getElementById("masterFolioStyle").value;
    const pos = document.getElementById("masterFolioPos").value;
    const rule = document.getElementById("masterHeaderRule").checked;
    const override4 = document.getElementById("masterOverrideP4").checked;

    const pages = [2, 3, 4, 5];
    document.getElementById("masterPagesGrid").innerHTML = pages
      .map((pg) => {
        const isLeft = pg % 2 === 0;
        const folio = formatFolio(pg, style);
        if (pg === 4 && override4) {
          return `
            <div class="master-page-card overridden">
              <div class="mono-val cyan">PAGE 4 • MASTER OVERRIDDEN</div>
              <div style="font-family:'Playfair Display',serif;font-size:18px;font-weight:700;">Full-Bleed Feature Opener</div>
              <div style="font-size:11px;color:#a6b0c8;">Running header &amp; folio suppressed locally.</div>
            </div>`;
        }
        const topLeft = pos === "top" && isLeft ? `<strong>${folio}</strong>` : header;
        const topRight = pos === "top" && !isLeft ? `<strong>${folio}</strong>` : pos === "top" ? header : "";
        const botLeft = pos === "outer" && isLeft ? `<strong>${folio}</strong>` : pos === "center" ? "" : footer;
        const botMid = pos === "center" ? `<strong>${folio}</strong>` : "";
        const botRight = pos === "outer" && !isLeft ? `<strong>${folio}</strong>` : pos === "center" ? "" : footer;

        return `
          <div class="master-page-card">
            <div class="mp-header ${rule ? "with-rule" : ""}">
              <span>${topLeft}</span><span>${topRight}</span>
            </div>
            <div style="font-size:11px;color:#444;margin:10px 0;">
              <strong>Page ${pg} (${isLeft ? "Verso / Left" : "Recto / Right"})</strong><br/>
              Inherits A-Master live folios &amp; running heads.
            </div>
            <div class="mp-footer">
              <span>${botLeft}</span><span>${botMid}</span><span>${botRight}</span>
            </div>
          </div>`;
      })
      .join("");
  }

  ["masterHeaderInput", "masterFooterInput", "masterFolioStyle", "masterFolioPos", "masterHeaderRule", "masterOverrideP4"].forEach(
    (id) => document.getElementById(id).addEventListener("input", renderMasters)
  );
  renderMasters();

  // ==================== 3A. ARTICLE HIERARCHY REPAIR ====================
  const HIER_CASES = [
    {
      id: 1,
      code: "Case 01: Floating Headline",
      kicker: "SWISS DESIGN • ESSAY",
      headline: "AVANT-GARDE\nPRINT GRIDS",
      pair: "A/V",
      pairIdx: 0,
      deck: "How postwar typographers tamed complex editorial copy using disciplined leading and optical spacing.",
      body: "When line spacing collapses on body text, ascenders collide with descenders and readers lose horizontal rhythm. Conversely, wide leading on a two-line display headline breaks unity so it reads like two unrelated titles.",
      broken: { kickerTrk: -40, headKern: 85, headTrk: 45, headLead: 1.62, deckTrk: 0, deckLead: 1.35, bodyTrk: 75, bodyLead: 1.04 },
      ideal: { kickerTrk: 130, headKern: -55, headTrk: -15, headLead: 1.12, deckTrk: 5, deckLead: 1.36, bodyTrk: 0, bodyLead: 1.48 }
    },
    {
      id: 2,
      code: "Case 02: Horsey Body",
      kicker: "MONOGRAPH • NO. 09",
      headline: "TOWARD\nQUIET FORM",
      pair: "T/O",
      pairIdx: 0,
      deck: "Concrete pavilions and quiet proportions reveal why negative space carries equal weight to ink.",
      body: "When body copy receives excessive leading above 1.75x, the eye struggles to leap from the end of one line to the start of the next. A standfirst deck bridges headline weight and body texture cleanly.",
      broken: { kickerTrk: -20, headKern: 75, headTrk: 70, headLead: 1.14, deckTrk: -50, deckLead: 1.76, bodyTrk: -10, bodyLead: 1.94 },
      ideal: { kickerTrk: 140, headKern: -50, headTrk: -15, headLead: 1.12, deckTrk: 5, deckLead: 1.34, bodyTrk: 0, bodyLead: 1.46 }
    },
    {
      id: 3,
      code: "Case 03: Colliding Display",
      kicker: "LETTERPRESS • ARCHIVE",
      headline: "WAX & LEAD\nTYPOGRAPHY",
      pair: "W/A",
      pairIdx: 0,
      deck: "From metal sorts locked in iron chases to Bézier outlines, optical spacing defines visual authority.",
      body: "Metal type founders shaved overhanging lead blocks—called kerns—so diagonal letters like W and A could nestle smoothly. Today optical kerning and thoughtful leading solve the exact same hierarchy challenge.",
      broken: { kickerTrk: -35, headKern: 95, headTrk: -55, headLead: 0.88, deckTrk: 55, deckLead: 1.0, bodyTrk: -48, bodyLead: 1.08 },
      ideal: { kickerTrk: 125, headKern: -60, headTrk: -10, headLead: 1.12, deckTrk: 0, deckLead: 1.35, bodyTrk: 0, bodyLead: 1.48 }
    }
  ];

  let hierIdx = 0;
  let hierState = { ...HIER_CASES[0].broken };
  let hierElem = "headline";
  let hierMetrics = true;
  let hierCompare = false;

  function evalHierarchy(c, s) {
    const checks = [
      {
        elem: "kicker",
        label: "All-Caps Kicker Tracking (+80 to +200)",
        pass: s.kickerTrk >= 80 && s.kickerTrk <= 200,
        msg: s.kickerTrk < 80 ? "Too tight for uppercase eyebrow." : s.kickerTrk > 200 ? "Too wide." : "Crisp uppercase tracking."
      },
      {
        elem: "headline",
        label: `Optical Kerning ${c.pair} (-95 to -20)`,
        pass: s.headKern >= -95 && s.headKern <= -20,
        msg: s.headKern > -20 ? `Close the wide gap between ${c.pair}.` : "Optical diagonal pair locked cleanly."
      },
      {
        elem: "headline",
        label: "Display Leading (1.02x–1.22x) & Snug Tracking",
        pass: s.headLead >= 1.02 && s.headLead <= 1.22 && s.headTrk >= -40 && s.headTrk <= 15,
        msg: s.headLead > 1.22 ? "Headline lines float too far apart." : s.headLead < 1.02 ? "Headline lines collide." : "Unified display block."
      },
      {
        elem: "deck",
        label: "Deck Leading (1.24x–1.46x) & Neutral Tracking",
        pass: s.deckLead >= 1.24 && s.deckLead <= 1.46 && s.deckTrk >= -15 && s.deckTrk <= 25,
        msg: s.deckLead < 1.24 || s.deckLead > 1.46 ? "Aim for 1.24x–1.46x leading." : "Balanced standfirst deck."
      },
      {
        elem: "body",
        label: "Body Reading Rhythm (1.38x–1.60x, -15..+20 Trk)",
        pass: s.bodyLead >= 1.38 && s.bodyLead <= 1.6 && s.bodyTrk >= -15 && s.bodyTrk <= 20,
        msg: s.bodyLead < 1.38 ? "Body lines feel cramped." : s.bodyLead > 1.6 ? "Body lines feel horsey/disconnected." : "Smooth editorial reading rhythm."
      }
    ];
    const score = Math.round((checks.filter((x) => x.pass).length / checks.length) * 100);
    return { checks, score, allPass: score === 100 };
  }

  function renderHierarchy() {
    const c = HIER_CASES[hierIdx];
    const disp = hierCompare ? c.broken : hierState;
    const ev = evalHierarchy(c, hierState);

    // Case chips
    document.getElementById("hierCaseChips").innerHTML = HIER_CASES.map(
      (item, i) => `<button class="filter-chip ${i === hierIdx ? "active" : ""}" data-hcase="${i}">${item.code}</button>`
    ).join("");
    document.querySelectorAll("[data-hcase]").forEach((b) =>
      b.addEventListener("click", () => {
        hierIdx = +b.dataset.hcase;
        hierState = { ...HIER_CASES[hierIdx].broken };
        hierCompare = false;
        renderHierarchy();
      })
    );

    const elemPass = (el) => ev.checks.filter((x) => x.elem === el).every((x) => x.pass);
    const guideCls = (el) =>
      `hier-region ${hierMetrics && !hierCompare ? "guides" : ""} ${elemPass(el) ? "pass" : ""} ${hierElem === el ? "selected" : ""}`;

    // Build headline with real per-pair kerning span
    const headHtml = c.headline
      .split("")
      .map((ch, idx) => {
        if (ch === "\n") return "<br/>";
        const isFirst = idx === c.pairIdx;
        const isSecond = idx === c.pairIdx + 1;
        const hl = hierMetrics && !hierCompare && hierElem === "headline" && (isFirst || isSecond);
        const ls = isFirst ? `${(disp.headKern / 1000) * 1.4}em` : `${(disp.headTrk / 1000) * 1.1}em`;
        return `<span class="${hl ? "kern-highlight" : ""}" style="letter-spacing:${ls};">${ch}</span>`;
      })
      .join("");

    document.getElementById("hierArticleSheet").innerHTML = `
      <div style="font-family:'JetBrains Mono',monospace;font-size:10px;color:${hierCompare ? "#ff4d4d" : "#666"};margin-bottom:8px;">
        ${hierCompare ? "ORIGINAL BROKEN PROOF" : "PICAPRESS EDITORIAL PROOF — CLICK ANY REGION"}
      </div>
      <div class="${guideCls("kicker")}" data-helem="kicker">
        ${hierMetrics && !hierCompare ? `<div class="region-meta"><span>KICKER</span><span>TRK ${disp.kickerTrk}</span></div>` : ""}
        <div style="font-family:'Space Grotesk',sans-serif;font-weight:700;font-size:11px;color:#b41f5e;letter-spacing:${(disp.kickerTrk / 1000) * 1.4}em;">${c.kicker}</div>
      </div>
      <div class="${guideCls("headline")}" data-helem="headline">
        ${hierMetrics && !hierCompare ? `<div class="region-meta"><span>HEADLINE</span><span>${c.pair} ${disp.headKern} • LD ${disp.headLead.toFixed(2)}x</span></div>` : ""}
        <div style="font-family:'Playfair Display',serif;font-weight:700;font-size:30px;line-height:${disp.headLead};">${headHtml}</div>
      </div>
      <div class="${guideCls("deck")}" data-helem="deck">
        ${hierMetrics && !hierCompare ? `<div class="region-meta"><span>DECK</span><span>TRK ${disp.deckTrk} • LD ${disp.deckLead.toFixed(2)}x</span></div>` : ""}
        <div style="font-family:'Space Grotesk',sans-serif;font-size:15px;color:#333;letter-spacing:${(disp.deckTrk / 1000) * 1.1}em;line-height:${disp.deckLead};">${c.deck}</div>
      </div>
      <div class="${guideCls("body")}" data-helem="body">
        ${hierMetrics && !hierCompare ? `<div class="region-meta"><span>BODY COPY</span><span>TRK ${disp.bodyTrk} • LD ${disp.bodyLead.toFixed(2)}x</span></div>` : ""}
        <div style="font-size:13.5px;color:#1e1c1a;letter-spacing:${(disp.bodyTrk / 1000) * 1.1}em;line-height:${disp.bodyLead};">${c.body}</div>
      </div>`;

    document.querySelectorAll("[data-helem]").forEach((el) =>
      el.addEventListener("click", () => {
        hierCompare = false;
        hierElem = el.dataset.helem;
        renderHierarchy();
      })
    );

    // Inspector Tabs
    const elems = [
      ["kicker", "Kicker"],
      ["headline", "Headline"],
      ["deck", "Deck"],
      ["body", "Body"]
    ];
    document.getElementById("hierInspectorTitle").textContent =
      softwareMode === "INDESIGN" ? "Character Panel (Id)" : "Measurements Palette (QXP)";
    document.getElementById("hierScoreBadge").textContent = `${ev.score}% Clarity`;
    document.getElementById("hierScoreBadge").className = `mono-tag ${ev.allPass ? "green" : "gold"}`;

    document.getElementById("hierElementTabs").innerHTML = elems
      .map(
        ([k, lbl]) =>
          `<button class="filter-chip ${hierElem === k ? "active" : ""}" data-htab="${k}">${elemPass(k) ? "● " : "○ "}${lbl}</button>`
      )
      .join("");
    document.querySelectorAll("[data-htab]").forEach((b) =>
      b.addEventListener("click", () => {
        hierElem = b.dataset.htab;
        renderHierarchy();
      })
    );

    // Sliders for active element
    const sliderHtml = (id, label, min, max, step, val, fmt) => `
      <div class="control-group">
        <label>${label} <span class="mono-val cyan">${fmt(val)}</span></label>
        <input id="${id}" type="range" min="${min}" max="${max}" step="${step}" value="${val}" class="studio-slider" />
      </div>`;

    let controls = "";
    if (hierElem === "kicker") {
      controls = sliderHtml("h_kickerTrk", "All-Caps Tracking (VA)", -80, 240, 5, hierState.kickerTrk, (v) => `${v}/1000 em`);
    } else if (hierElem === "headline") {
      controls =
        sliderHtml("h_headKern", `Pair Kerning (${c.pair})`, -120, 120, 5, hierState.headKern, (v) => `${v}/1000 em`) +
        sliderHtml("h_headTrk", "Headline Tracking (VA)", -80, 120, 5, hierState.headTrk, (v) => `${v}/1000 em`) +
        sliderHtml("h_headLead", "Headline Leading", 0.85, 1.8, 0.02, hierState.headLead, (v) => `${v.toFixed(2)}x`);
    } else if (hierElem === "deck") {
      controls =
        sliderHtml("h_deckTrk", "Deck Tracking (VA)", -80, 120, 5, hierState.deckTrk, (v) => `${v}/1000 em`) +
        sliderHtml("h_deckLead", "Deck Leading", 0.95, 1.85, 0.02, hierState.deckLead, (v) => `${v.toFixed(2)}x`);
    } else {
      controls =
        sliderHtml("h_bodyTrk", "Body Tracking (VA)", -80, 120, 5, hierState.bodyTrk, (v) => `${v}/1000 em`) +
        sliderHtml("h_bodyLead", "Body Leading", 0.95, 2.05, 0.02, hierState.bodyLead, (v) => `${v.toFixed(2)}x`);
    }
    document.getElementById("hierSlidersContainer").innerHTML = controls;

    const bind = (id, key, isFloat = false) => {
      const el = document.getElementById(id);
      if (el) {
        el.addEventListener("input", () => {
          hierState[key] = isFloat ? parseFloat(el.value) : parseInt(el.value, 10);
          renderHierarchy();
        });
      }
    };
    bind("h_kickerTrk", "kickerTrk");
    bind("h_headKern", "headKern");
    bind("h_headTrk", "headTrk");
    bind("h_headLead", "headLead", true);
    bind("h_deckTrk", "deckTrk");
    bind("h_deckLead", "deckLead", true);
    bind("h_bodyTrk", "bodyTrk");
    bind("h_bodyLead", "bodyLead", true);

    document.getElementById("hierChecklist").innerHTML = ev.checks
      .map(
        (ch) => `<div class="diag-item ${ch.pass ? "pass" : ""}"><strong>${ch.pass ? "✓" : "✕"} ${ch.label}</strong><span>${ch.msg}</span></div>`
      )
      .join("");
  }

  document.getElementById("hierMetricsBtn").addEventListener("click", (e) => {
    hierMetrics = !hierMetrics;
    e.target.textContent = hierMetrics ? "Metrics On" : "Clean Proof (W)";
    renderHierarchy();
  });
  document.getElementById("hierCompareBtn").addEventListener("click", (e) => {
    hierCompare = !hierCompare;
    e.target.textContent = hierCompare ? "Showing Broken" : "Compare Broken";
    renderHierarchy();
  });
  document.getElementById("hierResetBtn").addEventListener("click", () => {
    hierState = { ...HIER_CASES[hierIdx].broken };
    hierCompare = false;
    renderHierarchy();
  });
  document.getElementById("hierIdealBtn").addEventListener("click", () => {
    hierState = { ...HIER_CASES[hierIdx].ideal };
    hierCompare = false;
    renderHierarchy();
  });
  document.getElementById("hierCertifyBtn").addEventListener("click", () => {
    const ev = evalHierarchy(HIER_CASES[hierIdx], hierState);
    if (ev.allPass) {
      markLabComplete(`hier_${HIER_CASES[hierIdx].id}`, true);
      showToast("Editorial Hierarchy Certified! Progress saved.");
    } else {
      showToast(`Hierarchy at ${ev.score}% — fix remaining red items first.`);
    }
  });
  renderHierarchy();

  // ==================== 3B. FONT ANATOMY & KERNING ====================
  function renderAnatomy() {
    const kern = +document.getElementById("anatKern").value;
    const track = +document.getElementById("anatTrack").value;
    const lead = parseFloat(document.getElementById("anatLead").value);
    const font = document.getElementById("anatFont").value;

    document.getElementById("anatKernVal").textContent = `${kern}/1000 em`;
    document.getElementById("anatTrackVal").textContent = `${track}/1000 em`;
    document.getElementById("anatLeadVal").textContent = `${lead.toFixed(2)}x`;

    const spec = document.getElementById("anatomySpecimen");
    spec.style.fontFamily = font;
    spec.style.letterSpacing = `${(track / 1000) * 1.1}em`;
    document.getElementById("anatCharA").style.marginRight = `${(kern / 1000) * 1.4}em`;

    const preview = document.getElementById("anatomyLeadPreview");
    preview.style.fontFamily = font;
    preview.style.lineHeight = lead;
    preview.style.letterSpacing = `${(track / 1000) * 0.5}em`;
  }
  ["anatKern", "anatTrack", "anatLead", "anatFont"].forEach((id) =>
    document.getElementById(id).addEventListener("input", renderAnatomy)
  );
  renderAnatomy();

  // ==================== 3C. GRID DESIGN PRINCIPLES ====================
  let gridGuides = true;
  function renderGridLesson() {
    const cols = +document.getElementById("gridCols").value;
    const gutter = +document.getElementById("gridGutter").value;
    const margin = +document.getElementById("gridMargin").value;
    const align = document.getElementById("gridAlign").value;
    const imgSpan = document.getElementById("gridImageSpan").value;
    const rows = document.getElementById("gridModularRows").checked;

    document.getElementById("gridColsVal").textContent = `${cols} col${cols > 1 ? "s" : ""}`;
    document.getElementById("gridGutterVal").textContent = `${gutter} px`;
    document.getElementById("gridMarginVal").textContent = `${margin} px`;

    const spanCols =
      imgSpan === "full" ? cols : imgSpan === "span2" ? Math.min(2, cols) : 1;
    const offGridStyle = imgSpan === "offgrid" ? "transform:translate(14px,-6px) rotate(-1deg);border:2px solid #ff4d4d;" : "";

    const sampleCopy =
      "Modular Swiss grids establish disciplined visual rhythm, guiding eye movement across multi-column magazines and anchoring captions to photography.";

    const stage = document.getElementById("gridPreviewStage");
    stage.style.padding = `${margin}px`;
    stage.innerHTML = `
      ${
        gridGuides
          ? `<div style="position:absolute;inset:${margin}px;border:1.5px solid rgba(236,38,143,0.75);pointer-events:none;z-index:3;"></div>`
          : ""
      }
      ${
        gridGuides && rows
          ? `<div style="position:absolute;left:${margin}px;right:${margin}px;top:48%;border-top:1px dashed rgba(0,184,230,0.65);pointer-events:none;z-index:3;"></div>`
          : ""
      }
      <div style="display:grid;grid-template-columns:repeat(${cols},1fr);gap:${gutter}px;position:relative;z-index:2;">
        <div style="grid-column:span ${spanCols};height:110px;border-radius:4px;background:linear-gradient(135deg,#143c54,#00b8e6);color:#fff;padding:12px;display:flex;flex-direction:column;justify-content:flex-end;${offGridStyle}">
          <strong style="font-size:13px;">HERO ARTWORK (${imgSpan.toUpperCase()})</strong>
          <span style="font-size:10px;opacity:0.88;">${imgSpan === "offgrid" ? "Off-grid placement breaks structural tension" : "Snapped cleanly to column rails"}</span>
        </div>
        ${Array.from({ length: cols })
          .map(
            () => `
            <div style="background:${gridGuides ? "rgba(0,184,230,0.08)" : "transparent"};padding:4px;font-size:11.5px;line-height:1.45;text-align:${align};">
              ${sampleCopy}
            </div>`
          )
          .join("")}
      </div>`;

    const cpl = Math.round(78 / cols);
    const warning =
      imgSpan === "offgrid"
        ? "⚠ Image misaligned off-grid"
        : align === "justify" && cols >= 3
        ? "⚠ Narrow justified columns risk whitespace rivers"
        : gutter <= 8 && cols > 1
        ? "⚠ Tight gutter risks eye-jump across columns"
        : "✓ Balanced Swiss grid proportions";

    document.getElementById("gridTelemetry").innerHTML = `
      <span>Measure: ~${cpl} CPL</span>
      <span>Gutter: ${gutter}px • Margin: ${margin}px</span>
      <span>${warning}</span>`;
  }
  ["gridCols", "gridGutter", "gridMargin", "gridAlign", "gridImageSpan", "gridModularRows"].forEach((id) =>
    document.getElementById(id).addEventListener("input", renderGridLesson)
  );
  document.getElementById("gridGuidesBtn").addEventListener("click", (e) => {
    gridGuides = !gridGuides;
    e.target.textContent = gridGuides ? "Guides On" : "Clean Preview";
    renderGridLesson();
  });
  renderGridLesson();

  // ==================== 4A. LIVE PREFLIGHT CHALLENGE ====================
  let pfState = { oversetFixed: false, dpiFixed: false, fontFixed: false };
  function renderPreflight() {
    const errors = [
      {
        key: "oversetFixed",
        title: "Overset Text in Story Frame (+)",
        detail: "18 words hidden past bottom frame edge.",
        action: "Thread / Expand Frame"
      },
      {
        key: "dpiFixed",
        title: "Low-Resolution Link (72 PPI)",
        detail: "Hero_Cover.jpg below 300 PPI press threshold.",
        action: "Relink 300 PPI Master"
      },
      {
        key: "fontFixed",
        title: "Missing Typeface: Helvetica Neue Bold",
        detail: "Pink highlight indicates substituted fallback font.",
        action: "Activate & Sync Font"
      }
    ];
    const remaining = errors.filter((e) => !pfState[e.key]).length;
    const badge = document.getElementById("pfStatusLight");
    badge.textContent = remaining === 0 ? "0 ERRORS • READY" : `${remaining} ERROR${remaining > 1 ? "S" : ""}`;
    badge.className = `mono-tag ${remaining === 0 ? "green" : "red"}`;

    document.getElementById("pfDocStage").innerHTML = `
      <div style="display:flex;flex-direction:column;gap:10px;">
        <div style="font-family:${pfState.fontFixed ? "'Playfair Display',serif" : "monospace"};font-size:24px;font-weight:700;padding:6px;background:${pfState.fontFixed ? "transparent" : "rgba(236,38,143,0.22)"};border-radius:4px;">
          ${pfState.fontFixed ? "Autumn Architecture Issue" : "[MISSING FONT] Autumn Architecture Issue"}
        </div>
        <div style="height:105px;border-radius:6px;background:${pfState.dpiFixed ? "linear-gradient(135deg,#143c54,#10b981)" : "repeating-conic-gradient(#444 0% 25%, #666 0% 50%) 50% / 14px 14px"};color:#fff;display:grid;place-items:center;font-weight:700;font-size:12px;">
          ${pfState.dpiFixed ? "LINK OK • 300 PPI CMYK TIFF" : "⚠ LOW-RES 72 PPI IMAGE LINK"}
        </div>
        <div style="border:1.5px solid ${pfState.oversetFixed ? "#10b981" : "#ff4d4d"};padding:8px;border-radius:6px;position:relative;font-size:12px;">
          ${
            pfState.oversetFixed
              ? "All editorial copy fits cleanly inside the threaded text frames with zero overflow."
              : "Editorial story copy is clipped inside the text frame and truncates mid-sentence..."
          }
          ${
            pfState.oversetFixed
              ? ""
              : `<span style="position:absolute;right:6px;bottom:6px;background:#ff4d4d;color:#fff;font-weight:700;font-size:11px;padding:1px 5px;border-radius:3px;">+</span>`
          }
        </div>
      </div>`;

    document.getElementById("pfErrorList").innerHTML = errors
      .map(
        (err) => `
        <div class="diag-item ${pfState[err.key] ? "pass" : ""}" style="display:flex;justify-content:space-between;align-items:center;gap:8px;">
          <div>
            <strong>${pfState[err.key] ? "✓ Resolved:" : "✕"} ${err.title}</strong>
            <span>${err.detail}</span>
          </div>
          ${
            pfState[err.key]
              ? `<span class="mono-val green">FIXED</span>`
              : `<button class="btn btn-cyan btn-sm" data-pffix="${err.key}">${err.action}</button>`
          }
        </div>`
      )
      .join("");

    document.querySelectorAll("[data-pffix]").forEach((b) =>
      b.addEventListener("click", () => {
        pfState[b.dataset.pffix] = true;
        renderPreflight();
      })
    );
  }
  document.getElementById("pfResetBtn").addEventListener("click", () => {
    pfState = { oversetFixed: false, dpiFixed: false, fontFixed: false };
    renderPreflight();
  });
  document.getElementById("pfCertifyBtn").addEventListener("click", () => {
    if (pfState.oversetFixed && pfState.dpiFixed && pfState.fontFixed) {
      markLabComplete("preflight_1", true);
      showToast("Zero Preflight Errors — Package ready for commercial press!");
    } else {
      showToast("Resolve all red preflight errors before packaging.");
    }
  });
  renderPreflight();

  // ==================== 4B. COLOR MANAGEMENT & BLEED ====================
  const TARGET_CMYK = { c: 82, m: 24, y: 12, k: 8 };
  function cmykToRgb(c, m, y, k, profile) {
    const gain = profile === "uncoated" ? 0.88 : profile === "srgb" ? 1.12 : 1.0;
    const r = Math.round(255 * (1 - Math.min(1, (c / 100) * gain)) * (1 - k / 100));
    const g = Math.round(255 * (1 - Math.min(1, (m / 100) * gain)) * (1 - k / 100));
    const b = Math.round(255 * (1 - Math.min(1, (y / 100) * gain)) * (1 - k / 100));
    return `rgb(${r},${g},${b})`;
  }

  function renderColorLab() {
    const c = +document.getElementById("cmC").value;
    const m = +document.getElementById("cmM").value;
    const y = +document.getElementById("cmY").value;
    const k = +document.getElementById("cmK").value;
    const bleed = +document.getElementById("cmBleed").value;
    const profile = document.getElementById("cmProfile").value;

    document.getElementById("cmCVal").textContent = `${c}%`;
    document.getElementById("cmMVal").textContent = `${m}%`;
    document.getElementById("cmYVal").textContent = `${y}%`;
    document.getElementById("cmKVal").textContent = `${k}%`;
    document.getElementById("cmBleedVal").textContent = `${bleed} mm`;

    const targetColor = cmykToRgb(TARGET_CMYK.c, TARGET_CMYK.m, TARGET_CMYK.y, TARGET_CMYK.k, "fogra39");
    const userColor = cmykToRgb(c, m, y, k, profile);

    document.getElementById("cmTargetSwatch").style.background = targetColor;
    document.getElementById("cmUserSwatch").style.background = userColor;
    document.getElementById("cmTargetSpec").textContent = `C:${TARGET_CMYK.c} M:${TARGET_CMYK.m} Y:${TARGET_CMYK.y} K:${TARGET_CMYK.k} • FOGRA39`;

    const tac = c + m + y + k;
    const rawDelta = Math.sqrt((c - TARGET_CMYK.c) ** 2 + (m - TARGET_CMYK.m) ** 2 + (y - TARGET_CMYK.y) ** 2 + (k - TARGET_CMYK.k) ** 2) * 0.45 + (profile === "srgb" ? 18 : 0);
    const deltaE = rawDelta.toFixed(1);
    document.getElementById("cmUserSpec").textContent = `TAC: ${tac}% • ${profile === "srgb" ? "RGB GAMUT WARNING" : profile.toUpperCase()}`;
    const badge = document.getElementById("cmDeltaBadge");
    badge.textContent = `ΔE ${deltaE}`;
    badge.className = `mono-tag ${rawDelta <= 6.5 && profile !== "srgb" ? "green" : "gold"}`;

    const bleedPx = bleed * 4;
    document.getElementById("cmBleedDemo").innerHTML = `
      <div style="padding:${bleedPx}px;background:${bleed > 0 ? userColor : "#fff"};border:${bleed > 0 ? "1.5px dashed #ff4d4d" : "1.5px solid #ff4d4d"};transition:all 0.15s;">
        <div style="width:180px;height:68px;background:${userColor};border:1px solid rgba(0,0,0,0.45);display:grid;place-items:center;color:#fff;font-size:11px;font-weight:700;">
          ${bleed >= 3 ? "SAFE 3mm+ BLEED" : "⚠ WHITE SLIVER RISK"}
        </div>
      </div>`;
  }
  ["cmC", "cmM", "cmY", "cmK", "cmBleed", "cmProfile"].forEach((id) =>
    document.getElementById(id).addEventListener("input", renderColorLab)
  );
  document.getElementById("cmCheckBtn").addEventListener("click", () => {
    const c = +document.getElementById("cmC").value;
    const m = +document.getElementById("cmM").value;
    const y = +document.getElementById("cmY").value;
    const k = +document.getElementById("cmK").value;
    const bleed = +document.getElementById("cmBleed").value;
    const profile = document.getElementById("cmProfile").value;
    const dE = Math.sqrt((c - TARGET_CMYK.c) ** 2 + (m - TARGET_CMYK.m) ** 2 + (y - TARGET_CMYK.y) ** 2 + (k - TARGET_CMYK.k) ** 2) * 0.45;
    if (profile !== "srgb" && dE <= 6.5 && bleed >= 3) {
      markLabComplete("color_cal_1", true);
      showToast(`Color Proof Approved (ΔE ${dE.toFixed(1)}) with ${bleed}mm bleed!`);
    } else {
      showToast("Match ΔE ≤ 6.5 in CMYK (not sRGB) and set Bleed ≥ 3 mm.");
    }
  });
  renderColorLab();

  // ==================== 4C. PRINT-READY PDF EXPORT DIALOG ====================
  const PDF_BRIEFS = [
    {
      id: 1,
      code: "JOB-01 • Magazine Cover",
      spec: "Modern Offset RIP: Requires PDF/X-4:2008, Single Pages (not Spreads), 300 DPI, Crop Marks + 3mm Bleed, Coated CMYK (FOGRA39/SWOP) + Include Destination Profile.",
      eval: (s) => [
        { label: "PDF/X-4:2008 & Single Pages", pass: s.std === "x4" && !s.spreads },
        { label: "300 DPI + High/Lossless Compression", pass: s.dpi >= 300 && s.comp !== "jpeg_low" },
        { label: "Crop Marks + 3mm Bleed", pass: s.crop && s.bleed >= 3 },
        { label: "Coated CMYK + Included Profile", pass: s.conv === "preserve" && (s.profile === "fogra39" || s.profile === "swop") && s.incProf }
      ]
    },
    {
      id: 2,
      code: "JOB-02 • Legacy Art Book",
      spec: "Legacy PostScript RIP: Requires PDF/X-1a:2001, Single Pages, 300 DPI + ZIP Lossless, Crop + Registration + Color Bars + 3mm Bleed, GRACoL 2006 + Convert Spots.",
      eval: (s) => [
        { label: "PDF/X-1a:2001 Flattened Single Pages", pass: s.std === "x1a" && !s.spreads },
        { label: "300 DPI + ZIP Lossless", pass: s.dpi >= 300 && s.comp === "zip" },
        { label: "Crop, Registration, Color Bars & 3mm Bleed", pass: s.crop && s.reg && s.bars && s.bleed >= 3 },
        { label: "GRACoL 2006 + Convert Spots to Process", pass: s.conv === "preserve" && s.profile === "gracol" && s.spots }
      ]
    }
  ];
  let pdfBriefIdx = 0;

  function readPdfState() {
    return {
      std: document.getElementById("pdfStandard").value,
      spreads: document.getElementById("pdfSpreads").checked,
      dpi: +document.getElementById("pdfDpi").value,
      comp: document.getElementById("pdfComp").value,
      crop: document.getElementById("pdfCropMarks").checked,
      bleedMarks: document.getElementById("pdfBleedMarks").checked,
      reg: document.getElementById("pdfRegMarks").checked,
      bars: document.getElementById("pdfColorBars").checked,
      info: document.getElementById("pdfPageInfo").checked,
      bleed: +document.getElementById("pdfBleed").value,
      conv: document.getElementById("pdfColorConv").value,
      profile: document.getElementById("pdfProfile").value,
      incProf: document.getElementById("pdfIncludeProfile").checked,
      spots: document.getElementById("pdfConvertSpots").checked
    };
  }

  function applyPdfPreset(p) {
    const map = {
      smallest: { std: "none", spreads: true, dpi: 72, comp: "jpeg_low", conv: "none", profile: "srgb", incProf: false, spots: false },
      hq: { std: "none", spreads: false, dpi: 300, comp: "jpeg_max", conv: "none", profile: "swop", incProf: true, spots: false },
      x1a: { std: "x1a", spreads: false, dpi: 300, comp: "jpeg_max", conv: "preserve", profile: "swop", incProf: true, spots: true },
      x4: { std: "x4", spreads: false, dpi: 300, comp: "jpeg_max", conv: "preserve", profile: "fogra39", incProf: true, spots: false }
    }[p];
    if (!map) return;
    document.getElementById("pdfStandard").value = map.std;
    document.getElementById("pdfSpreads").checked = map.spreads;
    document.getElementById("pdfDpi").value = map.dpi;
    document.getElementById("pdfComp").value = map.comp;
    document.getElementById("pdfColorConv").value = map.conv;
    document.getElementById("pdfProfile").value = map.profile;
    document.getElementById("pdfIncludeProfile").checked = map.incProf;
    document.getElementById("pdfConvertSpots").checked = map.spots;
    renderPdfLab();
  }

  function renderPdfLab() {
    const s = readPdfState();
    const brief = PDF_BRIEFS[pdfBriefIdx];
    const checks = brief.eval(s);
    const estMb = (((s.dpi / 150) ** 2) * (s.comp === "zip" ? 1.6 : s.comp === "jpeg_low" ? 0.35 : 0.85) + 0.6).toFixed(1);

    document.getElementById("pdfBleedVal").textContent = `${s.bleed}.0 mm`;
    document.getElementById("pdfSizeTag").textContent = `Est. ${estMb} MB`;

    document.getElementById("pdfBriefChips").innerHTML = PDF_BRIEFS.map(
      (b, i) => `<button class="filter-chip ${i === pdfBriefIdx ? "active" : ""}" data-pbrief="${i}">${b.code}</button>`
    ).join("");
    document.querySelectorAll("[data-pbrief]").forEach((b) =>
      b.addEventListener("click", () => {
        pdfBriefIdx = +b.dataset.pbrief;
        renderPdfLab();
      })
    );

    document.getElementById("pdfBriefSummary").textContent = brief.spec;

    const bleedPx = s.bleed * 3;
    const isRgb = s.profile === "srgb";
    const sheetW = s.spreads ? 230 : 146;

    document.getElementById("pdfPressSheet").innerHTML = `
      ${
        s.bars
          ? `<div style="position:absolute;top:8px;display:flex;gap:2px;">
               ${["#00aeef","#ec008c","#fff200","#111","#2e3192","#ed1c24","#00a651","#999"].map((c) => `<span style="width:14px;height:8px;background:${c};border:1px solid #333;"></span>`).join("")}
             </div>`
          : ""
      }
      ${s.reg ? `<div style="position:absolute;top:6px;right:12px;font-size:14px;color:#111;">⊕</div><div style="position:absolute;bottom:6px;left:12px;font-size:14px;color:#111;">⊕</div>` : ""}
      <div style="position:relative;padding:${bleedPx}px;background:${s.bleed > 0 ? "#182032" : "transparent"};border:${s.bleed > 0 ? "1px solid #ff4d4d" : "none"};">
        ${s.crop ? `<div style="position:absolute;inset:-10px;border:1px dashed #111;pointer-events:none;"></div>` : ""}
        <div style="width:${sheetW}px;height:150px;background:#faf6ee;border:1px solid #888;padding:10px;display:flex;flex-direction:column;justify-content:space-between;color:#161513;">
          <div style="height:16px;background:${isRgb ? "#00e5ff" : "#143c54"};color:#fff;font-size:8px;font-weight:700;display:flex;align-items:center;padding:0 6px;">
            ${isRgb ? "RGB GAMUT WARNING" : "CALIBRATED CMYK MASTHEAD"}
          </div>
          <div style="flex:1;margin:6px 0;background:${s.dpi < 150 ? "repeating-conic-gradient(#555 0% 25%, #888 0% 50%) 50% / 10px 10px" : "linear-gradient(135deg,#2b4c7e,#ec268f)"};display:grid;place-items:center;color:#fff;font-size:9px;font-weight:700;">
            ${s.dpi} DPI • ${s.spreads ? "2-PG SPREAD" : "1-UP PAGE"}
          </div>
          <div style="font-size:7px;color:#555;">${s.std.toUpperCase()} • ${s.profile.toUpperCase()}</div>
        </div>
      </div>
      ${s.info ? `<div style="position:absolute;bottom:6px;font-family:'JetBrains Mono',monospace;font-size:9px;color:#333;">PicaPress_Export.pdf • Pg 1 • ${s.dpi}DPI • ${s.profile.toUpperCase()}</div>` : ""}`;

    document.getElementById("pdfLiveChecks").innerHTML = checks
      .map((c) => `<div class="diag-item ${c.pass ? "pass" : ""}"><strong>${c.pass ? "✓" : "✕"} ${c.label}</strong></div>`)
      .join("");
  }

  document.querySelectorAll(".dtab").forEach((btn) =>
    btn.addEventListener("click", () => {
      document.querySelectorAll(".dtab").forEach((b) => b.classList.toggle("active", b === btn));
      const map = { general: "pdfPanelGeneral", compression: "pdfPanelCompression", marks: "pdfPanelMarks", output: "pdfPanelOutput" };
      document.querySelectorAll(".dpanel").forEach((p) => p.classList.toggle("active", p.id === map[btn.dataset.dtab]));
    })
  );

  document.getElementById("pdfPresetSelect").addEventListener("change", (e) => applyPdfPreset(e.target.value));
  ["pdfStandard", "pdfSpreads", "pdfDpi", "pdfComp", "pdfCropMarks", "pdfBleedMarks", "pdfRegMarks", "pdfColorBars", "pdfPageInfo", "pdfBleed", "pdfColorConv", "pdfProfile", "pdfIncludeProfile", "pdfConvertSpots"].forEach(
    (id) => document.getElementById(id).addEventListener("input", renderPdfLab)
  );
  document.getElementById("pdfResetBtn").addEventListener("click", () => {
    document.getElementById("pdfPresetSelect").value = "smallest";
    ["pdfCropMarks", "pdfBleedMarks", "pdfRegMarks", "pdfColorBars", "pdfPageInfo"].forEach((id) => (document.getElementById(id).checked = false));
    document.getElementById("pdfBleed").value = 0;
    applyPdfPreset("smallest");
  });
  document.getElementById("pdfExportBtn").addEventListener("click", () => {
    const s = readPdfState();
    const checks = PDF_BRIEFS[pdfBriefIdx].eval(s);
    const passed = checks.filter((c) => c.pass).length;
    if (passed === checks.length) {
      markLabComplete(`pdf_${PDF_BRIEFS[pdfBriefIdx].id}`, true);
      showToast("PRESS-READY PDF CERTIFIED! RIP passed 100%.");
    } else {
      showToast(`RIP Rejected (${passed}/${checks.length} checks passed). Check failed items.`);
    }
  });
  applyPdfPreset("smallest");

  // ==================== 5. ID vs QXP SHOWDOWN QUIZ ====================
  const QUIZ = [
    { q: "InDesign calls flowing text around a photo 'Text Wrap'. What does QuarkXPress call it?", opts: ["Runaround", "Flow Dodge", "Slug Guard", "Kerning Wrap"], ans: 0 },
    { q: "What is the standard commercial print resolution for continuous-tone CMYK photography?", opts: ["72 DPI", "150 DPI", "300 DPI", "1200 DPI"], ans: 2 },
    { q: "How many PostScript points equal 1 Pica (1p0)?", opts: ["6 points", "10 points", "12 points", "72 points"], ans: 2 },
    { q: "Which PDF/X standard supports live transparency and ICC color management for modern RIPs?", opts: ["PDF/X-1a:2001", "PDF/X-4:2008", "Smallest File Size", "Acrobat 3"], ans: 1 },
    { q: "Adjusting space uniformly across an entire paragraph or uppercase kicker is called:", opts: ["Kerning", "Leading", "Tracking", "Bleed"], ans: 2 },
    { q: "What red symbol at the bottom-right of an InDesign text frame warns of hidden overset text?", opts: ["Red Plus [+]", "Cyan Star [*]", "Yellow Diamond", "Green Check"], ans: 0 }
  ];
  let qIdx = 0;
  let qScore = 0;
  let qAnswered = false;

  function renderQuiz() {
    const counter = document.getElementById("quizCounter");
    const body = document.getElementById("quizBody");
    if (qIdx >= QUIZ.length) {
      const pct = Math.round((qScore / QUIZ.length) * 100);
      if (pct > progress.bestQuiz) {
        progress.bestQuiz = pct;
        saveProgress();
      }
      markLabComplete("quiz_showdown", pct >= 80);
      counter.textContent = "COMPLETE";
      body.innerHTML = `
        <h3 style="margin-bottom:8px;">Final Score: ${qScore} / ${QUIZ.length} (${pct}%)</h3>
        <p class="muted" style="margin-bottom:14px;">Your best accuracy is saved locally in your browser.</p>
        <button id="quizRestart" class="btn btn-cyan">Play Showdown Again</button>`;
      document.getElementById("quizRestart").addEventListener("click", () => {
        qIdx = 0; qScore = 0; qAnswered = false; renderQuiz();
      });
      return;
    }
    const item = QUIZ[qIdx];
    counter.textContent = `Q ${qIdx + 1} / ${QUIZ.length}`;
    body.innerHTML = `
      <h3 style="margin-bottom:14px;">${item.q}</h3>
      <div>${item.opts.map((o, i) => `<button class="quiz-option" data-qopt="${i}">${o}</button>`).join("")}</div>`;
    document.querySelectorAll("[data-qopt]").forEach((btn) =>
      btn.addEventListener("click", () => {
        if (qAnswered) return;
        qAnswered = true;
        const chosen = +btn.dataset.qopt;
        if (chosen === item.ans) qScore += 1;
        document.querySelectorAll("[data-qopt]").forEach((b) => {
          const i = +b.dataset.qopt;
          if (i === item.ans) b.classList.add("correct");
          else if (i === chosen) b.classList.add("wrong");
        });
        setTimeout(() => {
          qIdx += 1;
          qAnswered = false;
          renderQuiz();
        }, 750);
      })
    );
  }
  renderQuiz();
  updateStatsUI();
})();
