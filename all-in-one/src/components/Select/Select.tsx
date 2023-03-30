"use client";

import React, { useState } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faX,
  faChevronUp,
  faChevronDown,
} from "@fortawesome/free-solid-svg-icons";

export function Select({
  options = [],
  selected = "",
  onSelected = (select) => {},
}: {
  options: string[];
  selected: string;
  onSelected: (value: string) => void;
}) {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <div className="flex flex-col items-center">
      <div className="flex flex-col items-center relative">
        <div className="w-full">
          <div className="my-2 bg-white p-1 flex border border-gray-200 rounded">
            <div className="flex flex-auto flex-wrap"></div>
            <input
              disabled
              value={selected}
              className="p-1 px-2 appearance-none outline-none w-full text-gray-800 "
            />

            <div className="text-gray-300 w-8 py-1 pl-2 pr-1 border-l flex items-center border-gray-200">
              <button
                className="cursor-pointer w-6 h-6 text-gray-600 outline-none focus:outline-none"
                onClick={() => {
                  setIsOpen(!isOpen);
                }}>
                <FontAwesomeIcon icon={isOpen ? faChevronUp : faChevronDown} />
              </button>
            </div>
          </div>
        </div>
        {isOpen && (
          <div className="absolute shadow top-[100%] z-40 w-full lef-0 text-black rounded max-h-select overflow-y-auto">
            <div className="flex flex-col w-full">
              {options.map((item) => (
                <div
                  className="cursor-pointer w-full border-gray-100 rounded-t border-b 
                hover:bg-teal-100"
                  key={item}
                  onClick={() => {
                    setIsOpen(false);
                    onSelected(item);
                  }}>
                  <div className="flex w-full items-center p-2 pl-2 border-transparent bg-white border-l-2 relative hover:bg-teal-600 hover:text-teal-100 hover:border-teal-600">
                    <div className="w-full items-center flex">
                      <div className="mx-2 leading-6  "> {item} </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
