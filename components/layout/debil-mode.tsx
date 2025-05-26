"use client";

import React, { useEffect, useState } from "react";

function DebilMode() {
  const [isDebil, setIsDebil] = useState(false);

  useEffect(() => {
    const debil = localStorage.getItem("debil");
    if (debil === "true") {
      setIsDebil(true);
    }
  }, []);

  useEffect(() => {
    const html = document.querySelector("html");
    if (isDebil) {
      html?.classList.add("debil");
      localStorage.setItem("debil", "true");
    } else {
      html?.classList.remove("debil");
      localStorage.removeItem("debil");
    }
  }, [isDebil]);

  return (
    <div>
      <button onClick={() => setIsDebil(!isDebil)}>Toggle debil mode</button>
    </div>
  );
}

export default DebilMode;
