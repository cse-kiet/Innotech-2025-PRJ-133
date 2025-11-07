import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './landing.css';

const LandingPage = () => {
  const navigate = useNavigate();
  const [currentStep, setCurrentStep] = useState(0);
  const [displayedText, setDisplayedText] = useState('');
  const [showTagline, setShowTagline] = useState(false);
  const fullText = "Welcome to ShelfGuardian";
  const tagline = "Never let anything expire unnoticed. Smart tracking for a smarter you.";

  useEffect(() => {
    // Step 0: Show intro
    if (currentStep === 0) {
      const timer = setTimeout(() => {
        setCurrentStep(1);
      }, 3000);
      return () => clearTimeout(timer);
    }

    // Step 1: Animated typing
    if (currentStep === 1) {
      let index = 0;
      const typingInterval = setInterval(() => {
        if (index < fullText.length) {
          setDisplayedText(fullText.substring(0, index + 1));
          index++;
        } else {
          clearInterval(typingInterval);
          setTimeout(() => {
            setShowTagline(true);
            setTimeout(() => {
              setCurrentStep(2);
            }, 3000);
          }, 500);
        }
      }, 100);
      return () => clearInterval(typingInterval);
    }

    // Step 2: Navigate to login after showing everything
    if (currentStep === 2) {
      const timer = setTimeout(() => {
        navigate('/login');
      }, 2000);
      return () => clearTimeout(timer);
    }
  }, [currentStep, fullText, navigate]);

  return (
    <div className="landing-container">
      <div className="animated-background">
        <div className="gradient-orb orb-1"></div>
        <div className="gradient-orb orb-2"></div>
        <div className="gradient-orb orb-3"></div>
        <div className="floating-shapes">
          <div className="shape shape-1"></div>
          <div className="shape shape-2"></div>
          <div className="shape shape-3"></div>
          <div className="shape shape-4"></div>
        </div>
      </div>

      <div className="landing-content">
        {currentStep === 0 && (
          <div className="intro-section fade-in">
            <div className="intro-icon">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M19 4H5C3.89543 4 3 4.89543 3 6V20C3 21.1046 3.89543 22 5 22H19C20.1046 22 21 21.1046 21 20V6C21 4.89543 20.1046 4 19 4Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                <path d="M16 2V6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                <path d="M8 2V6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                <path d="M3 10H21" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
            </div>
            <h2 className="intro-title">ShelfGuardian</h2>
            <p className="intro-description">
              Your intelligent companion for tracking expiration dates.<br />
              Stay organized, reduce waste, and never miss an expiry date again.
            </p>
          </div>
        )}

        {currentStep === 1 && (
          <div className="welcome-section">
            <div className="typing-text">
              {displayedText}
              <span className="cursor-blink">|</span>
            </div>
          </div>
        )}

        {showTagline && (
          <div className={`tagline-section ${showTagline ? 'slide-up' : ''}`}>
            <p className="tagline-text">{tagline}</p>
          </div>
        )}

        {currentStep === 2 && (
          <div className="transition-section fade-out">
            <div className="loading-dots">
              <span></span>
              <span></span>
              <span></span>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default LandingPage;

